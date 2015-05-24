package cc.devfun.pbrpc;

import com.google.protobuf.GeneratedMessage;

public class RequestMessage extends Message {
    public RequestMessage(int serviceId, int stamp, GeneratedMessage arg){
        super(serviceId, stamp, arg);
        setToRequest();
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return false;
    }
}
