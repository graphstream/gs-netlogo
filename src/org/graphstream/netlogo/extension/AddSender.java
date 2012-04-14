package org.graphstream.netlogo.extension;
import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.stream.netstream.NetStreamSender;
import org.graphstream.stream.netstream.packing.Base64Packer;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;


public class AddSender extends DefaultCommand {
	@Override
	public String getAgentClassString() {
		return "O";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[]{Syntax.TYPE_STRING, Syntax.TYPE_STRING, Syntax.TYPE_NUMBER});
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		String id;
		String host;
		int port;
		try {
			id = args[0].getString();
			host = args[1].getString();
			port = (int)args[2].getDoubleValue();
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
		
		NetStreamSender sender = null;
		try {
			sender = new NetStreamSender(host, port);
		} catch (UnknownHostException e) {
			throw new ExtensionException(e.getMessage());
		} catch (IOException e) {
			throw new ExtensionException(e.getMessage());			
		}
		sender.setPacker(new Base64Packer());
		GSExtension.addSender(id, sender);
	}
}
