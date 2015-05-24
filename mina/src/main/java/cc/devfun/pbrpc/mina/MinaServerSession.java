package cc.devfun.pbrpc.mina;

import java.net.SocketAddress;

import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.Message;
import cc.devfun.pbrpc.ResponseHandle;
import org.apache.mina.core.session.IoSession;

import cc.devfun.pbrpc.RpcSession;

public class MinaServerSession implements RpcSession {
    private IoSession ioSession;

    public MinaServerSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public void close() {
        ioSession.close(true);
    }

    @Override
    public void sendMessage(Message message) {
        ResponseHandle handle = message.getResponseHandle();
        if (message.isRequest() && handle != null) {
            if (handle.isSynchronizedRpc()) {
                throw new UnsupportedOperationException("MINA implementation don't support synchronized call, use asynchronous call to replace it.");
            }
        }
        ioSession.write(message);
    }

    public Object getAttribute(Object o) {
        return ioSession.getAttribute(o);
    }

    public Object setAttribute(Object o, Object o1) {
        return ioSession.setAttribute(o, o1);
    }

    public Object removeAttribute(Object o) {
        return ioSession.removeAttribute(o);
    }

    public boolean containsAttribute(Object o) {
        return ioSession.containsAttribute(o);
    }

    public boolean isConnected() {
        return ioSession.isConnected();
    }

    public SocketAddress getRemoteAddress() {
        return ioSession.getRemoteAddress();
    }

    public SocketAddress getLocalAddress() {
        return ioSession.getLocalAddress();
    }
}
