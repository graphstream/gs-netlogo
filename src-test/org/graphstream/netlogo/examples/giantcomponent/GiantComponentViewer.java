package org.graphstream.netlogo.examples.giantcomponent;
import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.thread.ThreadProxyPipe;


public class GiantComponentViewer {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Graph g = new SingleGraph("giant component");
		g.display();
		NetStreamReceiver receiver = new NetStreamReceiver(2012);
		ThreadProxyPipe pipe = receiver.getDefaultStream();
		pipe.addSink(g);
		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}
	}
}
