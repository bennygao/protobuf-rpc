package cc.devfun.pbrpc;

import com.google.protobuf.nano.MessageNano;

public class ResponseMessage extends Message {
    public ResponseMessage(int serviceId, int stamp, MessageNano arg){
        super(serviceId, stamp, arg);
        setToResponse();
    }

    @Override
    public Message createResponse(MessageNano arg) {
        throw new UnsupportedOperationException("cannot create response for response.");
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