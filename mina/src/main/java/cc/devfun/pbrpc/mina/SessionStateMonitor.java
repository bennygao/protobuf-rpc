package cc.devfun.pbrpc.mina;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface SessionStateMonitor {
    public void sessionClosed(IoSession session) throws Exception;
    public void sessionCreated(IoSession session) throws Exception;
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception;
    public void sessionOpened(IoSession session) throws Exception;
}
