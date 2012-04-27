package org.graphstream.netlogo.extension.receiver;

import org.graphstream.netlogo.extension.GSManager;
import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Link;
import org.nlogo.api.LogoException;
import org.nlogo.api.Observer;
import org.nlogo.api.Syntax;
import org.nlogo.api.Turtle;

/**
 * Implements the {@code add-attribute} reporter.
 * 
 * <pre>
 * gs:get-attribute receiverId attribute
 * </pre>
 * 
 * @author Stefan Balev
 * 
 */
public class GetAttribute extends DefaultReporter {
	@Override
	public String getAgentClassString() {
		return "OTL";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(
				new int[] { Syntax.StringType(), Syntax.StringType() },
				Syntax.ListType());
	}

	@Override
	public Object report(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		try {
			String receiverId = args[0].getString();
			GSReceiver receiver = GSManager.getReceiver(receiverId);
			String attribute = args[1].getString();
			Agent agent = context.getAgent();
			if (agent instanceof Observer)
				return receiver.receiveGraphAttribute(attribute);
			else if (agent instanceof Turtle)
				return receiver.receiveNodeAttribute(agent.id(), attribute);
			else if (agent instanceof Link) {
				Link link = (Link) agent;
				return receiver.receiveEdgeAttribute(link.end1().id(), link
						.end2().id(), attribute);
			}
			return null;
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

}
