package org.graphstream.netlogo.extension;

import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Link;
import org.nlogo.api.LogoException;
import org.nlogo.api.Turtle;

public class RemoveElement extends DefaultCommand {
	@Override
	public String getAgentClassString() {
		return "TL";
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		Agent agent = context.getAgent();
		if (agent instanceof Turtle)
			GSManager.removeNode(agent.id() + "");
		else if (agent instanceof Link) {
			Link link = (Link)agent;
			GSManager.removeEdge(link.end1().id() + "_" + link.end2().id());
		}
	}
}
