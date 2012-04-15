package org.graphstream.netlogo.extension;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;


public class RemoveSender extends DefaultCommand {
	@Override
	public String getAgentClassString() {
		return "O";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[]{Syntax.TYPE_STRING});
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		String id = null;
		try {
			id = args[0].getString();
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
		GSManager.removeSender(id);
	}
}
