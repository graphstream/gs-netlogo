package org.graphstream.netlogo.extension.receiver;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.netstream.NetStreamReceiver;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoList;

/**
 * A receiver.
 * 
 * A NetStreamReceiver plus helper methods that do the real job for all the
 * receiver primitives.
 * 
 * @author Stefan Balev
 * 
 */
public class GSReceiver extends SinkAdapter {
	protected SinkTime sinkTime;
	protected NetStreamReceiver nsReceiver;
	protected ThreadProxyPipe pipe;
	protected Attributes graphAttributes;
	protected Map<String, Attributes> nodeAttributes;
	protected Map<String, Attributes> edgeAttributes;
	protected Queue<Double> steps;
	protected Set<String> attributeFilter;

	public GSReceiver(SinkTime sinkTime, String host, int port,
			Set<String> attributeFilter) throws ExtensionException {
		this.sinkTime = sinkTime;
		this.attributeFilter = attributeFilter;
		try {
			nsReceiver = new NetStreamReceiver(host, port);
		} catch (UnknownHostException e) {
			throw new ExtensionException(e.getMessage());
		} catch (IOException e) {
			throw new ExtensionException(e.getMessage());
		}
		pipe = nsReceiver.getDefaultStream();
		pipe.addSink(this);
		graphAttributes = new Attributes();
		nodeAttributes = new HashMap<String, Attributes>();
		edgeAttributes = new HashMap<String, Attributes>();
		steps = new LinkedList<Double>();
	}

	public LogoList receiveGraphAttribute(String attribute) {
		pipe.pump();
		return graphAttributes.get(attribute);
	}

	public LogoList receiveNodeAttribute(long nodeId, String attribute) {
		pipe.pump();
		Attributes a = nodeAttributes.get(nodeId + "");
		return a == null ? LogoList.Empty() : a.get(attribute);
	}

	public LogoList receiveEdgeAttribute(long fromId, long toId,
			String attribute) {
		pipe.pump();
		Attributes a = edgeAttributes.get(fromId + "_" + toId);
		return a == null ? LogoList.Empty() : a.get(attribute);
	}

	public Double waitStep() {
		while (steps.isEmpty()) {
			try {
				pipe.blockingPump();
			} catch (InterruptedException e) {
			}
		}
		return steps.remove();
	}

	public void flush() {
		pipe.pump();
		graphAttributes = new Attributes();
		nodeAttributes.clear();
		edgeAttributes.clear();
		steps.clear();
	}

	public void close() {
		nsReceiver.quit();
	}

	// Sink methods

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)
				&& (attributeFilter == null || attributeFilter
						.contains(attribute))) {
			Attributes a = edgeAttributes.get(edgeId);
			if (a == null) {
				a = new Attributes();
				edgeAttributes.put(edgeId, a);
			}
			a.add(attribute, value);
		}
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		edgeAttributeAdded(sourceId, timeId, edgeId, attribute, newValue);
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)
				&& (attributeFilter == null || attributeFilter
						.contains(attribute)))
			graphAttributes.add(attribute, value);
	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		graphAttributeAdded(sourceId, timeId, attribute, newValue);
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		if (sinkTime.isNewEvent(sourceId, timeId)
				&& (attributeFilter == null || attributeFilter
						.contains(attribute))) {
			Attributes a = nodeAttributes.get(nodeId);
			if (a == null) {
				a = new Attributes();
				nodeAttributes.put(nodeId, a);
			}
			a.add(attribute, value);
		}
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		nodeAttributeAdded(sourceId, timeId, nodeId, attribute, newValue);
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		if (sinkTime.isNewEvent(sourceId, timeId))
			steps.add(step);
	}
}
