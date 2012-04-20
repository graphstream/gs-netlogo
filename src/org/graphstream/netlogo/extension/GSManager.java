package org.graphstream.netlogo.extension;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.netlogo.extension.sender.GSSender;
import org.graphstream.netlogo.extension.sender.Add;
import org.graphstream.netlogo.extension.sender.AddAttribute;
import org.graphstream.netlogo.extension.sender.Clear;
import org.graphstream.netlogo.extension.sender.Remove;
import org.graphstream.netlogo.extension.sender.RemoveAttribute;
import org.graphstream.netlogo.extension.sender.Step;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.stream.sync.SourceTime;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;

public class GSManager extends DefaultClassManager {
	private static String sourceId;
	private static SourceTime sourceTime;
	private static SinkTime sinkTime;

	private static Map<String, GSSender> senders;

	static {
		sourceId = "gs-netlogo@"
				+ ManagementFactory.getRuntimeMXBean().getName();
		sourceTime = new SourceTime(sourceId);
		sinkTime = new SinkTime();
		sourceTime.setSinkTime(sinkTime);
		senders = new HashMap<String, GSSender>();
	}

	@Override
	public void load(PrimitiveManager manager) throws ExtensionException {
		manager.addPrimitive("add-sender", new AddSender());
		manager.addPrimitive("remove-sender", new RemoveSender());

		manager.addPrimitive("add", new Add());
		manager.addPrimitive("remove", new Remove());

		manager.addPrimitive("add-attribute", new AddAttribute());
		manager.addPrimitive("remove-attribute", new RemoveAttribute());

		manager.addPrimitive("clear", new Clear());
		manager.addPrimitive("step", new Step());
	}

	public static GSSender getSender(String senderId) {
		return senders.get(senderId);
	}

	public static void addSender(String senderId, String host, int port) throws ExtensionException {
		GSSender sender = senders.get(senderId);
		if (sender == null) {
			sender = new GSSender(sourceId, sourceTime, host, port);
			senders.put(senderId, sender);
		}
	}

	public static void removeSender(String senderId) throws ExtensionException {
		GSSender sender = senders.get(senderId);
		if (sender != null) {
			sender.close();
			senders.remove(senderId);
		}
	}
}
