package cc.devfun.pbrpc.mina;

import static java.lang.String.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import cc.devfun.pbrpc.Endpoint;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
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
	private IoHandler ioHandler;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public MinaServerEndpoint(String name, SocketAddress addr, SessionStateMonitor monitor) {
		this.name = name;
		this.listenAddress = (InetSocketAddress) addr;
		this.ioHandler = new ProtobufRpcHandler(this, monitor);
	}

	public String getName() {
		return name;
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
				new ExecutorFilter(Executors.newCachedThreadPool()));

		// 设置session的handler
		acceptor.setHandler(ioHandler);

		// 设置read buffer size
		SocketSessionConfig config = acceptor.getSessionConfig();
		config.setReadBufferSize(8192);
		config.setSendBufferSize(8192);
		config.setTcpNoDelay(true);
		config.setKeepAlive(true);

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
