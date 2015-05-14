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

    @Override
    public void run() {
        GeneratedMessage returns = registry.invokeService(request.getServiceId(),
                request.getArgument(),
                session);
        Message response = new Message(request.getServiceId(),
                request.getStamp(), Message.STAGE_RESPONSE,
                returns);

        session.sendMessage(response);
    }
}
