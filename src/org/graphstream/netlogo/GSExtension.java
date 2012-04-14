package org.graphstream.netlogo;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.stream.netstream.NetStreamSender;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;


public class GSExtension extends DefaultClassManager {
	protected static Map<String, NetStreamSender> senders = new HashMap<String, NetStreamSender>();
	protected static long timeId = 0;

	@Override
	public void load(PrimitiveManager manager) throws ExtensionException {
		manager.addPrimitive("add-sender", new AddSender());
		manager.addPrimitive("remove-sender", new RemoveSender());
		manager.addPrimitive("clear", new Clear());
		
		manager.addPrimitive("add", new AddElement());
	}
	
	public static void addSender(String id, NetStreamSender sender) {
		senders.put(id, sender);
	}
	
	public static NetStreamSender removeSender(String id) {
		return senders.remove(id);
	}
	
	public static void addNode(String id) {
		for (Map.Entry<String, NetStreamSender> entry : senders.entrySet())
			entry.getValue().nodeAdded(entry.getKey(), timeId, id);
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
}
