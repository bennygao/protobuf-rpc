package cc.devfun.pbrpc.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {
	ByteBuffer buf;

	public ByteBufferOutputStream(ByteBuffer buf) {
		this.buf = buf;
	}

	public void write(int b) throws IOException {
		buf.put((byte) b);
	}

	public void write(byte[] bytes, int off, int len) throws IOException {
		buf.put(bytes, off, len);
	}
}
