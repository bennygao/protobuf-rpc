package com.yingshibao.foundation.rpc;

import com.google.protobuf.GeneratedMessage;

public class RequestHandle implements Runnable {
    private Message request;
    private RpcSession session;
    private ServiceRegistry registry;

    public RequestHandle(Message request, ServiceRegistry registry, RpcSession session) {
        this.request = request;
        this.registry = registry;
        this.session = session;
    }

    public RpcSession getSession() {
        return session;
    }

    public Message getRequest() {
        return request;
    }

    @Override
    public void run() {
        try {
            GeneratedMessage returns = registry.invokeService(request.getServiceId(),
                    request.getArgument(),
                    session);
            Message response = new ResponseMessage(request.getServiceId(),
                    request.getStamp(), returns);
            session.sendMessage(response);
        } catch (Throwable t) {
            Message response = new ResponseMessage(request.getServiceId(),
                    request.getStamp(), null);
            response.setServiceException();
            session.sendMessage(response);
        }
    }
}
