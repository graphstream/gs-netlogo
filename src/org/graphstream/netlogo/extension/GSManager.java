package org.graphstream.netlogo.extension;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.stream.netstream.NetStreamSender;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;


public class GSManager extends DefaultClassManager {
	protected static Map<String, NetStreamSender> senders = new HashMap<String, NetStreamSender>();
	protected static long timeId = 0;

	@Override
	public void load(PrimitiveManager manager) throws ExtensionException {
		manager.addPrimitive("add-sender", new AddSender());
		manager.addPrimitive("remove-sender", new RemoveSender());
		
		manager.addPrimitive("add", new AddElement());
		manager.addPrimitive("remove", new RemoveElement());
		
		manager.addPrimitive("add-attribute", new AddAttribute());
		manager.addPrimitive("remove-attribute", new RemoveAttribute());

		manager.addPrimitive("clear", new Clear());
		manager.addPrimitive("step", new Step());		
	}
	
	public static void addSender(String id, String host, int port) throws ExtensionException {
		NetStreamSender sender = senders.get(id);
		if (sender != null)
			throw new ExtensionException("Sender named " + id + " already exists. Remove it first.");
		try {
			sender = new NetStreamSender(host, port);
		} catch (UnknownHostException e) {
			throw new ExtensionException(e.getMessage());
		} catch (IOException e) {
			throw new ExtensionException(e.getMessage());
		}
		senders.put(id, sender);
	}
	
	public static void removeSender(String id) throws ExtensionException {
		NetStreamSender sender = senders.remove(id);
		if (sender == null)
			return;
		try {
			sender.close();
		} catch (IOException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public static void addNode(String nodeId) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().nodeAdded(entry.getKey(), timeId, nodeId);
		timeId++;
	}
	
	public static void addEdge(String fromId, String toId, boolean directed) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().edgeAdded(entry.getKey(), timeId, fromId + "_" + toId, fromId, toId, directed);
		timeId++;
	}
	
	public static void clear() {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().graphCleared(entry.getKey(), timeId);
		timeId++;
	}
	
	public static void removeNode(String nodeId) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().nodeRemoved(entry.getKey(), timeId, nodeId);
		timeId++;
	}
	
	public static void removeEdge(String edgeId) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().edgeRemoved(entry.getKey(), timeId, edgeId);
		timeId++;		
	}
	
	public static void addGraphAttribute(String attribute, Object value) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().graphAttributeAdded(entry.getKey(), timeId, attribute, value);
		timeId++;				
	}
	
	public static void addNodeAttribute(String nodeId, String attribute, Object value) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().nodeAttributeAdded(entry.getKey(), timeId, nodeId, attribute, value);
		timeId++;						
	}
	
	public static void addEdgeAttribute(String edgeId, String attribute, Object value) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().edgeAttributeAdded(entry.getKey(), timeId, edgeId, attribute, value);
		timeId++;		
	}
	
	public static void removeGraphAttribute(String attribute) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().graphAttributeRemoved(entry.getKey(), timeId, attribute);
		timeId++;		
	}
	
	public static void removeNodeAttribute(String nodeId, String attribute) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().nodeAttributeRemoved(entry.getKey(), timeId, nodeId, attribute);
		timeId++;		
	}

	public static void removeEdgeAttribute(String edgeId, String attribute) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().edgeAttributeRemoved(entry.getKey(), timeId, edgeId, attribute);
		timeId++;		
	}
	
	public static void stepBegins(double step) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().stepBegins(entry.getKey(), timeId, step);
		timeId++;
	}
}
