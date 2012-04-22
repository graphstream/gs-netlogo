package org.graphstream.netlogo.test;
import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.util.VerboseSink;


public class TestSender {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		NetStreamReceiver receiver = new NetStreamReceiver(2012);
		final ThreadProxyPipe pipe = receiver.getDefaultStream();
		pipe.addSink(new VerboseSink());
		
		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}		
	}

}
