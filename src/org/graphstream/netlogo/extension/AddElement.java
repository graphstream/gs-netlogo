package org.graphstream.netlogo.extension;
import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Link;
import org.nlogo.api.LogoException;
import org.nlogo.api.Turtle;


public class AddElement extends DefaultCommand {
	@Override
	public String getAgentClassString() {
		return "TL";
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		Agent agent = context.getAgent();
		if (agent instanceof Turtle)
			GSManager.addNode(agent.id() + "");
		else if (agent instanceof Link) {
			Link link = (Link)agent;
			GSManager.addEdge(link.end1().id() + "", link.end2().id() + "", link.isDirectedLink());
		}
	}
}
