package com.yingshibao.foundation.rpc.nio;

import java.io.IOException;
import java.io.InputStream;
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

import com.yingshibao.foundation.rpc.*;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public class NioSocketEndpoint extends Endpoint implements Runnable {
    class IoBufferHolder {
        private final static int DEFAULT_BUFFER_SIZE = 1024;
        private ByteBuffer ioBuffer = ByteBuffer
                .allocateDirect(DEFAULT_BUFFER_SIZE);

        ByteBuffer getIoBuffer() {
            return getIoBuffer(DEFAULT_BUFFER_SIZE);
        }

        synchronized ByteBuffer getIoBuffer(int expectedSize) {
            if (ioBuffer == null) {
                if (expectedSize <= DEFAULT_BUFFER_SIZE) {
                    ioBuffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
                } else {
                    ioBuffer = ByteBuffer.allocateDirect(expectedSize);
                }

            } else if (ioBuffer.capacity() < expectedSize) {
                ioBuffer = ByteBuffer.allocateDirect(expectedSize);
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
            GeneratedMessage arg = message.getArgument();
            if (arg != null) {
                messageSize += arg.getSerializedSize();
            }

            ByteBuffer buffer = getIoBuffer(messageSize);
            buffer.clear();
            buffer.putInt(messageSize);
            buffer.putInt(message.getStamp());
            buffer.putInt(message.getServiceId());
            buffer.put(message.getFeature());
            if (arg != null) {
                arg.writeTo(new ByteBufferOutputStream(buffer));
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

        Message recvMessage(SocketChannel channel) throws IOException {
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
                throws InvalidProtocolBufferException {
            int stamp = buffer.getInt();
            int serviceId = buffer.getInt();
            byte feature = buffer.get();
            GeneratedMessage arg = null;
            if (buffer.remaining() > 0) {
                InputStream is = new ByteBufferInputStream(buffer);
                ServiceRegistry registry = endpoint.getRegistry(serviceId);
                Parser<? extends GeneratedMessage> parser = (Message.isRequest(feature)) ?
                        registry.getParserForRequest(serviceId) :
                        registry.getParserForResponse(serviceId);
                arg = parser.parseFrom(is);
            }

            return Message.createMessage(serviceId, stamp, feature, arg);
        }
    }


    public final static int DEFAULT_EXECUTORS_NUM = 2;

    private SocketAddress remoteAddress;
    private Queue<Message> sendQueue;
    private SocketChannel remoteChannel;
    private Pipe pipe;
    private Pipe.SinkChannel sinkChannel;
    private Pipe.SourceChannel sourceChannel;
    private Selector selector;
    private ByteBuffer commandBuffer;
    private ByteBuffer cmdBuffer;
    private Receiver receiver;
    private Sender sender;
    private ExecutorService executors;
    private Map<Integer, ResponseHandle> stampsMap;
    private Thread thread;
    private int executorsNum;

    public NioSocketEndpoint(int executorsNum) {
        this.executorsNum = executorsNum;

        sendQueue = new ConcurrentLinkedQueue<>();
        cmdBuffer = ByteBuffer.allocateDirect(8);
        receiver = new Receiver(this);
        sender = new Sender();
        commandBuffer = ByteBuffer.allocate(1);
        stampsMap = new HashMap<>();

        remoteAddress = null;
        remoteChannel = null;
        pipe = null;
        sinkChannel = null;
        sourceChannel = null;
        selector = null;
        thread = null;
    }

    public NioSocketEndpoint() {
        this(DEFAULT_EXECUTORS_NUM);
    }

    public boolean connect(String remoteAddr, int port) {
        remoteAddress = new InetSocketAddress(remoteAddr, port);
        boolean succ = false;

        try {
            remoteChannel = SocketChannel.open();
            remoteChannel.connect(remoteAddress);
            remoteChannel.configureBlocking(false);
            succ = true;
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
        }

        return succ;
    }

    public void start() throws IOException {
        try {
            executors = Executors.newFixedThreadPool(2);
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

    public void stop() throws IOException {
        if (thread == null) {
            return;
        }

        cmdBuffer.clear();
        cmdBuffer.putInt(ControlCommand.stop.ordinal());
        cmdBuffer.flip();
        sinkChannel.write(cmdBuffer);

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
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (!handleKey(selectionKey)) { // 收到退出command
                        return;
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

    public synchronized void sendMessage(Message message) throws IOException {
        cmdBuffer.clear();
        cmdBuffer.putInt(ControlCommand.send_message.ordinal());
        cmdBuffer.flip();
        sendQueue.add(message);
        sinkChannel.write(cmdBuffer);
    }

    private void registerMessage(Message message) {
        ResponseHandle handle = message.getResponseHandle();
        if (handle != null) {
            stampsMap.put(message.getStamp(), handle);
        }
    }

    private void onReceivedMessage(Message message) throws Exception {
        int serviceId = message.getServiceId();
        if (message.isResponse()) { // 处理响应
            if (message.isServiceNotExist()) {
                System.err.println("remote endpoint does't supply service for serviceId " + serviceId);
            }

            ResponseHandle handle = stampsMap.remove(message.getStamp());
            if (handle == null) {
                System.err.println("unregistered handle for response: " + message);
            } else {
                handle.assignResponse(message);
                executors.submit(handle);
            }
        } else { // 处理请求
            ServiceRegistry registry = getRegistry(serviceId);
            if (registry == null) {
                System.err.println("unregistered handle for request: " + serviceId);
            } else {
                RequestHandle handle = new RequestHandle(message, registry, new NioSocketSession(this));
                executors.submit(handle);
            }
        }
    }

    private boolean handleKey(SelectionKey key) throws Exception {
        if (key.isReadable()) {
            SelectableChannel channel = key.channel();
            if (channel == sourceChannel) { // 有数据要发送
                commandBuffer.clear();
                sourceChannel.read(commandBuffer);
                commandBuffer.flip();
                if (commandBuffer.remaining() > 0) {
                    int cmd = commandBuffer.get();
                    if (cmd == ControlCommand.stop.ordinal()) {
                        return false;
                    } else {
                        Message message;
                        while ((message = sendQueue.poll()) != null) {
                            sender.sendMessage(message, remoteChannel);
                            registerMessage(message);
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

    private void close() {
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
        sendQueue.clear();
        executors.shutdown();
    }
}
