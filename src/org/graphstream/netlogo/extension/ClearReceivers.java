package org.graphstream.netlogo.extension;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

/**
 * Implements the {@code clear-receivers} command
 * 
 * <pre>
 * gs:clear-receivers
 * </pre>
 * 
 * @author Stefan Balev
 *
 */
public class ClearReceivers extends DefaultCommand {
	@Override
	public String getAgentClassString() {
		return "O";
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		GSManager.clearReceivers();
	}

}
