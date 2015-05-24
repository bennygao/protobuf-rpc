package cc.devfun.pbrpc.gencode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class LineEndFilterWriter extends Writer {
	private PipedReader pipedReader;
	private PipedWriter pipedWriter;
	private PrintWriter printWriter;
	private Thread thread;

	public LineEndFilterWriter(Writer writer) throws Exception {
		printWriter = new PrintWriter(writer);
		pipedReader = new PipedReader();
		pipedWriter = new PipedWriter(pipedReader);

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String line;
					BufferedReader reader = new BufferedReader(pipedReader);
					while ((line = reader.readLine()) != null) {
						printWriter.println(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		});

		thread.start();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		pipedWriter.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		pipedWriter.flush();
	}

	@Override
	public void close() throws IOException {
		pipedWriter.close();
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		printWriter.close();
	}
}
