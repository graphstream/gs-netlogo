package org.graphstream.netlogo.extension;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.graphstream.netlogo.extension.receiver.GSReceiver;
import org.graphstream.netlogo.extension.receiver.GetAttribute;
import org.graphstream.netlogo.extension.receiver.WaitStep;
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
	private static Map<String, GSReceiver> receivers;

	static {
		sourceId = "gs-netlogo@"
				+ ManagementFactory.getRuntimeMXBean().getName();
		sourceTime = new SourceTime(sourceId);
		sinkTime = new SinkTime();
		sourceTime.setSinkTime(sinkTime);
		senders = new HashMap<String, GSSender>();
		receivers = new HashMap<String, GSReceiver>();
	}

	@Override
	public void load(PrimitiveManager manager) throws ExtensionException {
		manager.addPrimitive("add-sender", new AddSender());
		manager.addPrimitive("clear-senders", new ClearSenders());

		manager.addPrimitive("add", new Add());
		manager.addPrimitive("remove", new Remove());

		manager.addPrimitive("add-attribute", new AddAttribute());
		manager.addPrimitive("remove-attribute", new RemoveAttribute());

		manager.addPrimitive("clear", new Clear());
		manager.addPrimitive("step", new Step());

		manager.addPrimitive("add-receiver", new AddReceiver());
		manager.addPrimitive("clear-receivers", new ClearReceivers());

		manager.addPrimitive("get-attribute", new GetAttribute());
		manager.addPrimitive("wait-step", new WaitStep());
	}

	public static GSSender getSender(String senderId) throws ExtensionException {
		GSSender sender = senders.get(senderId);
		if (sender == null)
			throw new ExtensionException("Sender \"" + senderId
					+ "\" does not exist");
		return sender;
	}

	public static void addSender(String senderId, String host, int port)
			throws ExtensionException {
		GSSender sender = senders.get(senderId);
		if (sender != null)
			throw new ExtensionException("Sender \"" + senderId
					+ "\" already exists");
		sender = new GSSender(sourceId, sourceTime, host, port);
		senders.put(senderId, sender);
	}

	public static void clearSenders() throws ExtensionException {
		for (GSSender sender : senders.values())
			sender.close();
		senders.clear();
	}

	public static GSReceiver getReceiver(String receiverId)
			throws ExtensionException {
		GSReceiver receiver = receivers.get(receiverId);
		if (receiver == null)
			throw new ExtensionException("Receiver \"" + receiverId
					+ "\" does not exist");
		return receiver;
	}

	public static void addReceiver(String receiverId, String host, int port, Set<String> attributeFilter)
			throws ExtensionException {
		GSReceiver receiver = receivers.get(receiverId);
		if (receiver != null)
			throw new ExtensionException("Receiver \"" + receiverId
					+ "\" already exists");
		receiver = new GSReceiver(sinkTime, host, port, attributeFilter);
		receivers.put(receiverId, receiver);
	}

	public static void clearReceivers() {
		for (GSReceiver receiver : receivers.values())
			receiver.close();
		receivers.clear();
	}
}
