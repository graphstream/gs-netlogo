package org.graphstream.netlogo.examples.aids;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.swingViewer.Viewer;

public class AidsExample {

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		// couple graph viewer
		simpleNetStreamViewer("localhost", 3001, false);
		// infection graph viewer
		simpleNetStreamViewer("localhost", 3002, true);
		// cumulative graph viewer
		new Thread(new CumulativeGraphThread("localhost", 3003)).start();
	}

	public static Viewer simpleNetStreamViewer(String host, int port,
			boolean autoLayout) throws UnknownHostException, IOException {
		NetStreamReceiver receiver = new NetStreamReceiver(host, port);
		
		// This beautiful code must stay commented waiting for
		// solution of issue # 57 on GS
//		ThreadProxyPipe pipe = receiver.getDefaultStream();
//		Viewer viewer = new Viewer(pipe);
//		viewer.addView(Viewer.DEFAULT_VIEW_ID, Viewer.newGraphRenderer());
//		if (autoLayout)
//			viewer.enableAutoLayout(Layouts.newLayoutAlgorithm());
//		return viewer;
		
		Graph g = new SingleGraph("useless");
		final ThreadProxyPipe pipe = receiver.getDefaultStream();
		pipe.addSink(g);
		
		new Thread() {
			public void run() {
				while (true) {
					pipe.pump();
					try {
						sleep(10);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		
		Viewer viewer = g.display(autoLayout);
		viewer.getDefaultView().resizeFrame(500, 500);
		return viewer;
	}

}
