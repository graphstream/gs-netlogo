package org.graphstream.netlogo.examples.aids;

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
}
