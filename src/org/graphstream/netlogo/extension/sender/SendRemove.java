package org.graphstream.netlogo.extension.sender;

import org.graphstream.netlogo.extension.GSManager;
import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Link;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;
import org.nlogo.api.Turtle;

public class SendRemove extends DefaultCommand {

	@Override
	public String getAgentClassString() {
		return "TL";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[] { Syntax.TYPE_STRING });
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		try {
			String senderId = args[0].getString();
			GSSender sender = GSManager.getSender(senderId);
			if (sender == null)
				return;
			Agent agent = context.getAgent();
			if (agent instanceof Turtle)
				sender.sendNodeRemoved(agent.id());
			else if (agent instanceof Link) {
				Link link = (Link) agent;
				sender.sendEdgeRemoved(link.end1().id(), link.end2().id());
			}
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

}
