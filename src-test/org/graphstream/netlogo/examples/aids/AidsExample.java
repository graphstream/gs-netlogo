package org.graphstream.netlogo.examples.aids;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.layout.Layouts;
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
		
		ThreadProxyPipe pipe = receiver.getDefaultStream();
		Viewer viewer = new Viewer(pipe);
		viewer.addView(Viewer.DEFAULT_VIEW_ID, Viewer.newGraphRenderer());
		if (autoLayout)
			viewer.enableAutoLayout(Layouts.newLayoutAlgorithm());
		viewer.getDefaultView().resizeFrame(500, 500);
		return viewer;		
	}

}
