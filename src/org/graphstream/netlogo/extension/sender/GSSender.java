package org.graphstream.netlogo.extension.sender;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamSender;
import org.graphstream.stream.sync.SourceTime;
import org.nlogo.api.ExtensionException;

public class GSSender {
	protected String sourceId;
	protected SourceTime sourceTime;
	protected NetStreamSender nsSender;
	
	public GSSender(String sourceId, SourceTime sourceTime, String host, int port) throws ExtensionException {
		this.sourceId = sourceId;
		this.sourceTime = sourceTime;
		try {
			nsSender = new NetStreamSender(host, port);
		} catch (UnknownHostException e) {
			throw new ExtensionException(e.getMessage());
		} catch (IOException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void sendNodeAdded(long nodeId) {
		nsSender.nodeAdded(sourceId, newEvent(), nodeId + "");
	}
	
	public void sendEdgeAdded(long fromId, long toId, boolean directed) {
		nsSender.edgeAdded(sourceId, newEvent(), fromId + "_" + toId, fromId + "", toId + "", directed);
	}
	
	public void sendNodeRemoved(long nodeId) {
		nsSender.nodeRemoved(sourceId, newEvent(), nodeId + "");
	}
	
	public void sendEdgeRemoved(long fromId, long toId) {
		nsSender.edgeRemoved(sourceId, newEvent(), fromId + "_" + toId);
	}
	
	public void sendGraphAttributeAdded(String attribute, Object value) {
		nsSender.graphAttributeAdded(sourceId, newEvent(), attribute, value);
	}
	
	public void sendNodeAttributeAdded(long nodeId, String attribute, Object value) {
		nsSender.nodeAttributeAdded(sourceId, newEvent(), nodeId + "", attribute, value);
	}
	
	public void sendEdgeAttributeAdded(long fromId, long toId, String attribute, Object value) {
		nsSender.edgeAttributeAdded(sourceId, newEvent(), fromId + "_" + toId, attribute, value);
	}
	
	public void sendGraphAttributeRemoved(String attribute) {
		nsSender.graphAttributeRemoved(sourceId, newEvent(), attribute);
	}
	
	public void sendNodeAttributeRemoved(long nodeId, String attribute) {
		nsSender.nodeAttributeRemoved(sourceId, newEvent(), nodeId + "", attribute);
	}
	
	public void sendEdgeAttributeRemoved(long fromId, long toId, String attribute) {
		nsSender.edgeAttributeRemoved(sourceId, newEvent(), fromId + "_" + toId, attribute);
	}
	
	public void sendGraphCleared() {
		nsSender.graphCleared(sourceId, newEvent());
	}
	
	public void sendStepBegins(double step) {
		nsSender.stepBegins(sourceId, newEvent(), step);
	}
	
	public void close() throws ExtensionException {
		try {
			nsSender.close();
		} catch (IOException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	protected long newEvent() {
		return sourceTime.newEvent();
	}
	
}
