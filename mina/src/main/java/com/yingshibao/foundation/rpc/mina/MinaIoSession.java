package com.yingshibao.foundation.rpc.mina;

import java.net.SocketAddress;

import com.yingshibao.foundation.rpc.Endpoint;
import com.yingshibao.foundation.rpc.ResponseHandle;
import org.apache.mina.core.session.IoSession;

import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.RpcSession;

public class MinaIoSession implements RpcSession {
    private IoSession ioSession;

    public MinaIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public void close() {
        ioSession.close(true);
    }

    @Override
    public void sendMessage(Message message) {
        ResponseHandle handle = message.getResponseHandle();
        if (message.isRequest() && handle != null) {
            if (handle.getStrategy() == Endpoint.RpcStrategy.sync) {
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
