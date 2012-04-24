package org.graphstream.netlogo.extension;

import java.util.HashSet;
import java.util.Set;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

public class AddReceiver extends DefaultCommand {
	@Override
	public String getAgentClassString() {
		return "O";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(
				new int[] { Syntax.StringType(), Syntax.StringType(),
						Syntax.NumberType(),
						Syntax.StringType() | Syntax.RepeatableType() }, 3);
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		try {
			String receiverId = args[0].getString();
			String host = args[1].getString();
			int port = args[2].getIntValue();
			Set<String> attributeFilter = null;
			if (args.length > 3) {
				attributeFilter = new HashSet<String>();
				for (int i = 3; i < args.length; i++)
					attributeFilter.add(args[i].getString());
			}
			GSManager.addReceiver(receiverId, host, port, attributeFilter);
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
}
