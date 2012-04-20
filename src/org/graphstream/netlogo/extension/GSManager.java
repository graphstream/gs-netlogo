package org.graphstream.netlogo.extension;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.netlogo.extension.sender.GSSender;
import org.graphstream.netlogo.extension.sender.SendAdd;
import org.graphstream.netlogo.extension.sender.SendAddAttribute;
import org.graphstream.netlogo.extension.sender.SendClear;
import org.graphstream.netlogo.extension.sender.SendRemove;
import org.graphstream.netlogo.extension.sender.SendRemoveAttribute;
import org.graphstream.netlogo.extension.sender.SendStep;
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

		manager.addPrimitive("send-add", new SendAdd());
		manager.addPrimitive("send-remove", new SendRemove());

		manager.addPrimitive("send-add-attribute", new SendAddAttribute());
		manager.addPrimitive("send-remove-attribute", new SendRemoveAttribute());

		manager.addPrimitive("send-clear", new SendClear());
		manager.addPrimitive("send-step", new SendStep());
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
