package cc.devfun.pbrpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cc.devfun.pbrpc.*;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public class NioClientEndpoint extends Endpoint implements Runnable {
    enum ControlCommand {
        send_message,
        stop
    }

    class IoBufferHolder {
        final static int DEFAULT_BUFFER_SIZE = 1024;
        protected byte[] flatArray;
        private ByteBuffer ioBuffer;

        IoBufferHolder() {
            flatArray = new byte[DEFAULT_BUFFER_SIZE];
            ioBuffer = ByteBuffer.wrap(flatArray);
        }

        ByteBuffer getIoBuffer() {
            return getIoBuffer(DEFAULT_BUFFER_SIZE);
        }

        synchronized ByteBuffer getIoBuffer(int expectedSize) {
            if (flatArray.length < expectedSize) {
                flatArray = new byte[expectedSize];
                ioBuffer = ByteBuffer.wrap(flatArray);
            }

            return ioBuffer;
        }
    }

    class Sender extends IoBufferHolder {
        Sender() {
        }

        void sendMessage(Message message, SocketChannel channel)
                throws IOException {
            int messageSize = 9; // int32(stamp) + int32(serviceId) + byte(feature)
            MessageNano arg = message.getArgument();
            int serializedSize = 0;
            if (arg != null) {
                serializedSize = arg.getSerializedSize();
                messageSize += serializedSize;
            }

            ByteBuffer buffer = getIoBuffer(messageSize);
            buffer.clear();
            buffer.putInt(messageSize);
            buffer.putInt(message.getStamp());
            buffer.putInt(message.getServiceId());
            buffer.put(message.getFeature());
            if (arg != null) {
                arg.writeTo(CodedOutputByteBufferNano.newInstance(flatArray, buffer.position(), serializedSize));
                buffer.position(buffer.position() + serializedSize);
            }

            buffer.flip();
            while ((buffer.remaining()) > 0) {
                channel.write(buffer);
            }
        }
    }

    class Receiver extends IoBufferHolder {
        private final static int PHASE_INIT = 0;
        private final static int PHASE_RECV_MESSAGE_SIZE = 1;
        private final static int PHASE_RECV_MESSAGE_BODY = 2;

        private int currentPhase;
        private int messageSize = 0;
        private Endpoint endpoint;

        Receiver(Endpoint endpoint) {
            this.endpoint = endpoint;
            reset();
        }

        void reset() {
            currentPhase = PHASE_INIT;
        }

        Message recvMessage(SocketChannel channel) throws IllegalAccessException, InstantiationException, IOException {
            ByteBuffer buffer;

            if (currentPhase == PHASE_INIT) {
                buffer = getIoBuffer();
                buffer.clear();
                buffer.limit(4);
                currentPhase = PHASE_RECV_MESSAGE_SIZE;
                return null;
            } else if (currentPhase == PHASE_RECV_MESSAGE_SIZE) {
                buffer = getIoBuffer();
                if (channel.read(buffer) <= 0) { // socket be closed by peer.
                    throw new IllegalStateException("socket channel be closed by peer.");
                }

                if (buffer.remaining() != 0) {
                    return null;
                } else {
                    buffer.flip();
                    messageSize = buffer.getInt();
                    if (messageSize < 9) { // stamp(i32/4) + serviceId(i32/4) +
                        // stage(byte/1)
                        throw new IllegalArgumentException("error Message-Size:"
                                + messageSize);
                    }

                    currentPhase = PHASE_RECV_MESSAGE_BODY;
                    buffer = getIoBuffer(messageSize);
                    buffer.clear();
                    buffer.limit(messageSize);
                    return null;
                }
            } else {
                buffer = getIoBuffer();
                if (channel.read(buffer) <= 0) { // socket be closed by peer.
                    throw new IllegalStateException("socket channel be closed by peer.");
                }

                if (buffer.remaining() != 0) {
                    return null;
                } else {
                    buffer.flip();
                    Message message = parseMessage(buffer);
                    currentPhase = PHASE_INIT;
                    return message;
                }
            }
        }

        private Message parseMessage(ByteBuffer buffer)
                throws IllegalAccessException, InstantiationException, IOException {
            int stamp = buffer.getInt();
            int serviceId = buffer.getInt();
            byte feature = buffer.get();
            MessageNano arg = null;
            Message message = null;
            if (buffer.remaining() > 0) {
                ServiceRegistry registry = endpoint.getRegistry(serviceId);
                if (registry == null) {
                    message = Message.createMessage(serviceId, stamp, feature, null);
                    message.setServiceNotExist();
                    // 略去protobuf的数据
                    buffer.position(buffer.position() + buffer.remaining());
                } else {
                    Class<? extends MessageNano> clazz = (Message.isRequest(feature)) ?
                            registry.getClassForRequest(serviceId) :
                            registry.getClassForResponse(serviceId);
                    arg = clazz.newInstance();
                    arg.mergeFrom(CodedInputByteBufferNano.newInstance(flatArray, buffer.position(), buffer.remaining()));
                    message = Message.createMessage(serviceId, stamp, feature, arg);
                }
            } else {
                message = Message.createMessage(serviceId, stamp, feature, null);
            }

            return message;
        }
    }

    class AsyncTaskResult {
        boolean success;
        Throwable cause;

        AsyncTaskResult(boolean success) {
            this(success, null);
        }

        AsyncTaskResult(boolean success, Throwable cause) {
            this.success = success;
            this.cause = cause;
        }

        public boolean isSuccess() {
            return success;
        }

        public Throwable getCause() {
            return cause;
        }
    }

    interface AsyncTask {
        boolean run() throws Exception;
    }


    public final static int DEFAULT_EXECUTORS_NUM = 1;
    public final static int DEFAULT_HEARTBEAT_INTERVAL = 30;

    private SocketAddress remoteAddress;
    private Queue<Message> sendQueue;
    private SocketChannel remoteChannel;
    private Pipe pipe;
    private Pipe.SinkChannel sinkChannel;
    private Pipe.SourceChannel sourceChannel;
    private Selector selector;

    private ByteBuffer commandRxBuffer;
    private ByteBuffer commandTxBuffer;
    private Receiver receiver;
    private Sender sender;

    private ExecutorService executors;
    private int executorsNum;
    private Lock asyncTaskLock;

    private Map<Integer, ResponseHandle> stampsMap;
    private Thread thread;
    private boolean isCheckingHeartbeat;
    private int heartbeatInterval;
    private Message heartbeatRequest;

    public NioClientEndpoint() {
        this(DEFAULT_HEARTBEAT_INTERVAL, DEFAULT_EXECUTORS_NUM);
    }

    public NioClientEndpoint(int hbi) {
        this(hbi, DEFAULT_EXECUTORS_NUM);
    }

    public NioClientEndpoint(int hbi, int executorsNum) {
        sendQueue = new ConcurrentLinkedQueue<>();
        commandTxBuffer = ByteBuffer.allocateDirect(1);
        commandRxBuffer = ByteBuffer.allocate(1);
        receiver = new Receiver(this);
        sender = new Sender();
        stampsMap = new HashMap<>();

        remoteAddress = null;
        remoteChannel = null;
        pipe = null;
        sinkChannel = null;
        sourceChannel = null;
        selector = null;
        thread = null;
        isCheckingHeartbeat = false;
        heartbeatInterval = hbi;
        heartbeatRequest = Message.createHeartbeatMessage();

        this.executorsNum = executorsNum > 0 ? executorsNum : DEFAULT_EXECUTORS_NUM;
        executors = Executors.newFixedThreadPool(this.executorsNum);
        asyncTaskLock = new ReentrantLock();
    }

    private void asyncTask(AsyncTask task) {
        asyncTask(task, false);
    }

    private AsyncTaskResult asyncTask(final AsyncTask task, boolean waitingResult) {
        asyncTaskLock.lock();
        final LinkedBlockingQueue<AsyncTaskResult> resultQueue = new LinkedBlockingQueue<>(1);
        try {
            executors.submit(new Runnable() {
                @Override
                public void run() {
                    boolean success = false;
                    Throwable cause = null;
                    try {
                        success = task.run();
                    } catch (Exception e) {
                        cause = e;
                    }
                    resultQueue.add(new AsyncTaskResult(success, cause));
                }
            });

            if (waitingResult) {
                try {
                    AsyncTaskResult result = resultQueue.take();
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new AsyncTaskResult(false, e);
                }
            } else {
                return null;
            }
        } finally {
            asyncTaskLock.unlock();
        }
    }

    public boolean connect(final String remoteAddr, final int port) {
        AsyncTaskResult result = asyncTask(new AsyncTask() {
            @Override
            public boolean run() throws Exception {
                try {
                    remoteAddress = new InetSocketAddress(remoteAddr, port);
                    remoteChannel = SocketChannel.open();
                    remoteChannel.connect(remoteAddress);
                    remoteChannel.configureBlocking(false);
                    return true;
                } catch (Exception e) {
                    if (remoteChannel != null) {
                        try {
                            remoteChannel.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            remoteChannel = null;
                        }
                    }

                    throw e;
                }
            }
        }, true);

        return result.isSuccess();
    }

    public void sendMessage(final Message message) throws IOException {
        if (thread == null) {
            throw new IllegalStateException("endpoint hasn't started.");
        }

        asyncTask(new AsyncTask() {
            @Override
            public boolean run() throws Exception {
                send(message);
                return true;
            }
        });
    }

    private synchronized void send(Message message) throws IOException {
        commandTxBuffer.clear();
        commandTxBuffer.put((byte) ControlCommand.send_message.ordinal());
        commandTxBuffer.flip();
        sendQueue.add(message);
        sinkChannel.write(commandTxBuffer);
    }

    @Override
    public void start() throws IOException {
        try {
            pipe = Pipe.open();
            sinkChannel = pipe.sink();
            sourceChannel = pipe.source();
            sourceChannel.configureBlocking(false);
            selector = Selector.open();

            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    @Override
    public synchronized void stop() throws IOException {
        if (thread == null) {
            return;
        }

        commandTxBuffer.clear();
        commandTxBuffer.put((byte) ControlCommand.stop.ordinal());
        commandTxBuffer.flip();
        sinkChannel.write(commandTxBuffer);

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();
    }

    @Override
    public void run() {
        try {
            remoteChannel.register(selector, SelectionKey.OP_READ);
            sourceChannel.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException cce) {
            close();
            return;
        }

        while (true) {
            try {
                selector.select(heartbeatInterval * 1000L);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                if (iterator.hasNext()) {
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (!handleKey(selectionKey)) { // 收到退出command
                            return;
                        }
                    }
                } else { // 空闲
                    if (isCheckingHeartbeat) { // 已经检测heartbeat，又再超时，认为连接已经中断。
                        return;
                    } else {
                        send(heartbeatRequest);
                        isCheckingHeartbeat = true;
                    }
                }
            } catch (ClosedSelectorException cse) { // Endpoint关闭
                return;
            } catch (Exception e) {
                e.printStackTrace();
                close();
                return;
            }
        }
    }

    private void registerMessage(Message message) {
        ResponseHandle handle = message.getResponseHandle();
        if (handle != null) {
            stampsMap.put(message.getStamp(), handle);
        }
    }

    private void onHeartbeatMessage(Message message) throws Exception {
        if (message.isRequest()) {
            send(message.createResponse(null));
        } else {
            isCheckingHeartbeat = false;
        }
    }

    private void onReceivedMessage(Message message) throws Exception {
        int serviceId = message.getServiceId();
        if (serviceId == 0) { // 收到heartbeat检测消息
            onHeartbeatMessage(message);
        } else if (message.isResponse()) { // 处理响应
            ResponseHandle handle = stampsMap.remove(message.getStamp());
            if (handle == null) {
                System.err.println("unregistered handle for response: " + message);
            } else {
                handle.assignResponse(message);
                executors.submit(handle);
            }
        } else { // 处理请求
            ServiceRegistry registry = getRegistry(serviceId);
            if (registry == null) { // 请求调用的服务未注册
                Message response = new ResponseMessage(serviceId, message.getStamp(), null);
                response.setServiceNotExist();
                send(response);
            } else {
                RequestHandle handle = new RequestHandle(message, registry, new NioClientSession(this));
                executors.submit(handle);
            }
        }
    }

    private void onSendRequestError(Message request) {
        try {
            ResponseHandle handle = request.getResponseHandle();
            if (handle != null) {
                ResponseMessage response = new ResponseMessage(request.getServiceId(), request.getStamp(), null);
                response.setIllegalArgument();
                handle.assignResponse(response);
                executors.submit(handle);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void onSendResponseError(Message response) {
        try {
            Message newone = new ResponseMessage(response.getServiceId(), response.getStamp(), null);
            newone.setServiceException();
            send(newone);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void onSendError(Message message) {
       if (message.isRequest()) {
           onSendRequestError(message);
       } else {
           onSendResponseError(message);
       }
    }

    private boolean handleKey(SelectionKey key) throws Exception {
        if (key.isReadable()) {
            SelectableChannel channel = key.channel();
            if (channel == sourceChannel) { // 有数据要发送
                commandRxBuffer.clear();
                sourceChannel.read(commandRxBuffer);
                commandRxBuffer.flip();
                if (commandRxBuffer.remaining() > 0) {
                    int cmd = commandRxBuffer.get();
                    if (cmd == ControlCommand.stop.ordinal()) {
                        return false;
                    } else {
                        Message message;
                        while ((message = sendQueue.poll()) != null) {
                            try {
                                sender.sendMessage(message, remoteChannel);
                                registerMessage(message);
                            } catch (Throwable t) {
                                onSendError(message);
                            }
                        }
                    }
                }
            } else { // 有数据可接收
                Message message = receiver.recvMessage(remoteChannel);
                if (message == null) { // 网络数据没到全，消息尚未接收完。
                    return true;
                } else {
                    System.out.println("received message: " + message);
                    onReceivedMessage(message);
                }
            }
        }

        return true;
    }

    private synchronized void close() {
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (pipe != null) {
            try {
                sourceChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sourceChannel = null;
            }

            try {
                sinkChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sinkChannel = null;
            }

            pipe = null;
        }

        if (remoteChannel != null) {
            try {
                remoteChannel.shutdownInput();
                remoteChannel.shutdownOutput();
                remoteChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                remoteChannel = null;
            }
        }

        selector = null;
        thread = null;
        isCheckingHeartbeat = false;
        sendQueue.clear();
    }

    public void destroy() throws Exception {
        stop();
        executors.shutdown();
    }
}
