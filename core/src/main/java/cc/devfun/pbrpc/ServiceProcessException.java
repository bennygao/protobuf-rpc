package cc.devfun.pbrpc;

public class ServiceProcessException extends RuntimeException {
    private RequestHandle requestHandle;

    public ServiceProcessException(Throwable cause, RequestHandle handle) {
        super(cause);
        this.requestHandle = handle;
    }

    public RequestHandle getRequestHandle() {
        return requestHandle;
    }
}
