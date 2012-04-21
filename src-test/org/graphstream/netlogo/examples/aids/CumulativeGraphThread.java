package org.graphstream.netlogo.examples.aids;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.swingViewer.Viewer;


public class CumulativeGraphThread extends SinkAdapter implements Runnable {
	Graph graph;
	ThreadProxyPipe pipe;
	public CumulativeGraphThread(String host, int port) throws UnknownHostException, IOException {
		NetStreamReceiver receiver = new NetStreamReceiver(host, port);
		pipe = receiver.getDefaultStream();
		
		graph = new SingleGraph("cumulated graph");
		graph.setStrict(false);
		pipe.addSink(graph);
		graph.addSink(this);

		Viewer viewer = graph.display();
		viewer.getDefaultView().resizeFrame(500, 500);

	}

	public void run() {
		while (true) {
			pipe.pump();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		int d = (int)Toolkit.diameter(graph);
		System.out.printf("step %8.0f | diameter %6d%n", step, d);
	}

	
	

}
