package org.graphstream.netlogo.examples.aids;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.ui.swingViewer.Viewer;

public class SimpleNetStreamViewer extends Viewer {
	public SimpleNetStreamViewer(NetStreamReceiver receiver) {
		this(receiver, true);
	}
	
	public SimpleNetStreamViewer(NetStreamReceiver receiver, boolean autoLayout) {
		super(receiver.getDefaultStream());
		addDefaultView(true);
		if (autoLayout)
			enableAutoLayout();
	}
	
	public SimpleNetStreamViewer(NetStreamReceiver receiver, boolean autoLayout, int width, int height) {
		this(receiver, autoLayout);
		getDefaultView().resizeFrame(width, height);
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		// couple graph viewer
		new SimpleNetStreamViewer(new NetStreamReceiver(2001), false, 500, 500);
		// infection graph viewer
		new SimpleNetStreamViewer(new NetStreamReceiver(2002), true, 500, 500);
		// cumulative graph viewer
		new SimpleNetStreamViewer(new NetStreamReceiver(2003), true, 500, 500);
	}
}
