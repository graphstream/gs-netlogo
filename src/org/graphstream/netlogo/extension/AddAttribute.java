package org.graphstream.netlogo.extension;

import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Link;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoList;
import org.nlogo.api.Observer;
import org.nlogo.api.Syntax;
import org.nlogo.api.Turtle;

public class AddAttribute extends DefaultCommand {

	@Override
	public String getAgentClassString() {
		return "OTL";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[]{Syntax.TYPE_STRING, 
				Syntax.TYPE_NUMBER | Syntax.TYPE_BOOLEAN | Syntax.TYPE_STRING | Syntax.TYPE_LIST});
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		try {
			Agent agent = context.getAgent();
			String attribute = args[0].getString();
			Object value = args[1].get();
			
			if (isOfSimpleType(value)) {
				addAttribute(agent, attribute, value);
				return;
			}
			
			LogoList list = args[1].getList();
			if (list.isEmpty())
				throw new ExtensionException("The list must not be empty");
			Class<?> elementClass = list.get(0).getClass();
			for (Object o : list) {
				if (!isOfSimpleType(o))
					throw new ExtensionException("The list elements must be of type boolean, number or string");
				if (!o.getClass().equals(elementClass))
					throw new ExtensionException("The list elements must be all of same type");
			}
			addAttribute(agent, attribute, list.toArray());
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	protected static void addAttribute(Agent agent, String attribute, Object value) {
		if (agent instanceof Observer)
			GSManager.addGraphAttribute(attribute, value);
		else if (agent instanceof Turtle)
			GSManager.addNodeAttribute(agent.id() + "", attribute, value);
		else if (agent instanceof Link) {
			Link link = (Link)agent;
			GSManager.addEdgeAttribute(link.end1().id() + "_" + link.end2().id(), attribute, value);
		}
	}
	
	protected static boolean isOfSimpleType(Object o) {
		return (o != null) && (o instanceof Boolean || o instanceof Double || o instanceof String);
	}
}
