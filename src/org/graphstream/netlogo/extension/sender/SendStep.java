package org.graphstream.netlogo.extension.sender;

import org.graphstream.netlogo.extension.GSManager;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

public class SendStep extends DefaultCommand {

	@Override
	public String getAgentClassString() {
		return "O";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[] { Syntax.TYPE_STRING,
				Syntax.TYPE_NUMBER });
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		try {
			String senderId = args[0].getString();
			GSSender sender = GSManager.getSender(senderId);
			if (sender == null)
				return;
			double step = args[1].getDoubleValue();
			sender.sendStepBegins(step);
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

}
