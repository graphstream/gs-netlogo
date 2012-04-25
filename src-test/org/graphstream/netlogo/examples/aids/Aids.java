package org.graphstream.netlogo.examples.aids;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.netstream.NetStreamSender;

public class Aids {

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		// couple graph viewer
		new SimpleNetStreamViewer(new NetStreamReceiver(2001), false, 500, 500);

		// infection graph viewer
		new SimpleNetStreamViewer(new NetStreamReceiver(2002), true, 500, 500);
		
		// cumulative graph viewer
		NetStreamReceiver receiver = new NetStreamReceiver(2003);
		new SimpleNetStreamViewer(receiver, true, 500, 500);
		new CumulativeGraphAnalyser(receiver, new NetStreamSender(3001));	
	}

}
