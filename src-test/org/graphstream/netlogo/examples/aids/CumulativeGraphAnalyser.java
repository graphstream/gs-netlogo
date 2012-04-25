package org.graphstream.netlogo.examples.aids;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.netstream.NetStreamSender;

public class CumulativeGraphAnalyser extends SinkAdapter{
	private NetStreamSender sender;
	private Graph graph;
	private String mySourceId;
	private long myTimeId;
	
	public CumulativeGraphAnalyser(NetStreamReceiver receiver, NetStreamSender sender) {
		this.sender = sender;
		graph = new SingleGraph("cumulative graph", false, false);
		ProxyPipe pipe = receiver.getDefaultStream();
		pipe.addElementSink(graph);
		pipe.addElementSink(this);
		
		mySourceId = toString();
		myTimeId = 0;
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		// diameter
		double diameter = 0;
		if (Toolkit.isConnected(graph))
			diameter = Toolkit.diameter(graph);
		sender.graphAttributeAdded(mySourceId, myTimeId++, "diameter", diameter);
		
		// degrees
		for (Node node : graph)
			sender.nodeAttributeAdded(mySourceId, myTimeId++, node.getId(), "degree", node.getDegree());
		
		// sync
		sender.stepBegins(mySourceId, myTimeId++, step);
	}
}
