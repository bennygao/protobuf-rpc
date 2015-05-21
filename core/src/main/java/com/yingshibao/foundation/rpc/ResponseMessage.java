package com.yingshibao.foundation.rpc;

import com.google.protobuf.GeneratedMessage;

public class ResponseMessage extends Message {
    public ResponseMessage(int serviceId, int stamp, GeneratedMessage arg){
        super(serviceId, stamp, arg);
        setToResponse();
    }


    @Override
    public boolean isRequest() {
        return false;
    }

    @Override
    public boolean isResponse() {
        return true;
    }
}