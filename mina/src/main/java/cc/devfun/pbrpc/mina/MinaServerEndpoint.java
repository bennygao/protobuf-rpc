package cc.devfun.pbrpc.mina;

import static java.lang.String.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import cc.devfun.pbrpc.Endpoint;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinaServerEndpoint extends Endpoint {
	private String name;
	private InetSocketAddress listenAddress;
	private NioSocketAcceptor acceptor;
    private List<ExecutorService> executors;
	private IoHandler ioHandler;
    private int executorsNum;
    private int heartbeatInterval;
	private SessionStateMonitor monitor;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public MinaServerEndpoint(String name, SocketAddress addr, int executorsNum, int hbInterval, SessionStateMonitor monitor) {
		this.name = name;
		this.listenAddress = (InetSocketAddress) addr;
        this.executorsNum = executorsNum;
        this.heartbeatInterval = hbInterval;
		this.monitor = monitor;
	}

	public String getName() {
		return name;
	}


    public int getExecutorsNum() {
        return executorsNum;
    }

    public void setExecutorsNum(int executorsNum) {
        this.executorsNum = executorsNum;
    }


    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

	@Override
	public void start() throws IOException {
		acceptor = new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors() + 1);
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

		// 增加Logger
		LoggingFilter loggingFilter = new LoggingFilter();
		loggingFilter.setMessageReceivedLogLevel(LogLevel.WARN);
		loggingFilter.setMessageSentLogLevel(LogLevel.WARN);
		loggingFilter.setSessionClosedLogLevel(LogLevel.WARN);
		loggingFilter.setSessionCreatedLogLevel(LogLevel.WARN);
		loggingFilter.setSessionOpenedLogLevel(LogLevel.WARN);
		chain.addLast("logging", loggingFilter);

		// 设置encoder/decoder
		chain.addLast("codec", new ProtocolCodecFilter(
				new MessageCodecFactory()));

		// 设置ExecutorFilter(MINA 2.0开始要求)
		// 设置Handler的初始线程数和最大线程数
		chain.addLast("threadPool",
				new ExecutorFilter(Executors.newSingleThreadExecutor()));

		// 设置session的handler
        executors = new ArrayList<>(executorsNum);
        for (int i = 0; i < executorsNum; ++i) {
            executors.add(Executors.newSingleThreadExecutor());
        }
		this.ioHandler = new ProtobufRpcHandler(this, executors, monitor);
		acceptor.setHandler(ioHandler);

		// 设置read buffer size
		SocketSessionConfig config = acceptor.getSessionConfig();
		config.setReadBufferSize(8192);
		config.setSendBufferSize(8192);
		config.setTcpNoDelay(true);
		config.setKeepAlive(true);
		config.setIdleTime(IdleStatus.BOTH_IDLE, heartbeatInterval);

		// 绑定地址并开始监听端口
		acceptor.setReuseAddress(true);
		acceptor.bind(listenAddress);
		logger.info(format("Endpoint %s listened on %s.", getName(),
				listenAddress.toString()));
	}

	@Override
	public void stop() throws Exception {
		acceptor.unbind();
		for (IoSession session : acceptor.getManagedSessions().values()) {
			session.close(true);
		}

        for (ExecutorService e : executors) {
            e.shutdown();
        }

        executors.clear();
	}

	public long getActivationTime() {
		return acceptor.getActivationTime();
	}

	public long getAliveTime() {
		return System.currentTimeMillis() - acceptor.getActivationTime();
	}

	public int getManagedSessionCount() {
		return acceptor.getManagedSessionCount();
	}

	public long getRxBytes() {
		if (acceptor == null) {
			return 0;
		}
		return acceptor.getStatistics().getReadBytes();
	}

	public long getTxBytes() {
		if (acceptor == null) {
			return 0;
		}
		return acceptor.getStatistics().getWrittenBytes();
	}
}
