package org.graphstream.netlogo.extension;

import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Link;
import org.nlogo.api.LogoException;
import org.nlogo.api.Observer;
import org.nlogo.api.Syntax;
import org.nlogo.api.Turtle;

public class RemoveAttribute extends DefaultCommand {

	@Override
	public String getAgentClassString() {
		return "OTL";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[]{Syntax.TYPE_STRING});
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		String attribute;
		Agent agent;
		try {
			agent = context.getAgent();
			attribute = args[0].getString();
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}

		if (agent instanceof Observer)
			GSManager.removeGraphAttribute(attribute);
		else if (agent instanceof Turtle)
			GSManager.removeNodeAttribute(agent.id() + "", attribute);
		else if (agent instanceof Link) {
			Link link = (Link)agent;
			GSManager.removeEdgeAttribute(link.end1().id() + "_" + link.end2().id(), attribute);
		}
	}
}
