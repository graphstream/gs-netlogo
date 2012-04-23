package org.graphstream.netlogo.examples.giantcomponent;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.netstream.NetStreamReceiver;

public class GiantComponentViewer {
	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		NetStreamReceiver receiver = new NetStreamReceiver(2012);
		ProxyPipe pipe = receiver.getDefaultStream();
		Graph g = new SingleGraph("giant component");
		g.display();
		pipe.addSink(g);
		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}
	}
}
