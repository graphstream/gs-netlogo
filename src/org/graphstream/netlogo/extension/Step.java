package org.graphstream.netlogo.extension;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

public class Step extends DefaultCommand {

	@Override
	public strictfp String getAgentClassString() {
		return "O";
	}

	@Override
	public strictfp Syntax getSyntax() {
		return Syntax.commandSyntax(new int[]{Syntax.TYPE_NUMBER});
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		double step;
		try {
			step = args[0].getDoubleValue();
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
		GSManager.stepBegins(step);
	}

}
