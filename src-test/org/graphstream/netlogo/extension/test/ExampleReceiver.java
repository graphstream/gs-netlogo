package org.graphstream.netlogo.extension.test;
import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.netstream.packing.Base64Unpacker;
import org.graphstream.stream.thread.ThreadProxyPipe;


public class ExampleReceiver {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Graph g = new SingleGraph("graph");
		g.setStrict(false);
		g.display();
		NetStreamReceiver receiver = new NetStreamReceiver(2012);
		ThreadProxyPipe pipe = receiver.getDefaultStream();
		pipe.addSink(g);
		receiver.setUnpacker(new Base64Unpacker());
		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}
	}
}
