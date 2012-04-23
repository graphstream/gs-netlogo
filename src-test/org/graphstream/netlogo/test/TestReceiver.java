package org.graphstream.netlogo.test;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamSender;

public class TestReceiver {

	public static void main(String[] args) throws UnknownHostException, IOException {
		NetStreamSender sender = new NetStreamSender(3012);
		long timeId = 0;
		
		sender.graphAttributeAdded("foo", timeId++, "a", 1);
		sender.graphAttributeAdded("foo", timeId++, "a", true);
		sender.graphAttributeAdded("foo", timeId++, "a", "bar");
		Integer[] array = new Integer[3];
		array[0] = 0; array[1] = 1; array[2] = 2;
		sender.graphAttributeAdded("foo", timeId++, "a", array);
		sender.graphAttributeAdded("foo", 0, "a", "old");
		
		sender.stepBegins("foo", timeId++, 1);
		
		sender.nodeAttributeAdded("foo", timeId++, "0", "na", "bar");
		sender.nodeAttributeChanged("foo", timeId++, "0", "na", "bar", "newbar");
		
		sender.stepBegins("foo", timeId++, 2);
		
		sender.edgeAttributeAdded("foo", timeId++, "0_1", "ea", array);
		sender.close();
	}

}
