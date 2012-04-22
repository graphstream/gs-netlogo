package org.graphstream.netlogo.extension.receiver;

import org.graphstream.netlogo.extension.GSManager;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

public class WaitStep extends DefaultReporter {
	@Override
	public String getAgentClassString() {
		return "O";
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(new int[] { Syntax.TYPE_STRING },
				Syntax.TYPE_NUMBER);
	}

	@Override
	public Object report(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		try {
			String receiverId = args[0].getString();
			GSReceiver receiver = GSManager.getReceiver(receiverId);
			return receiver.waitStep();
		} catch (LogoException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

}
