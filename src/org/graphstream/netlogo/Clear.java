package org.graphstream.netlogo;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;


public class Clear extends DefaultCommand {
	

	@Override
	public strictfp String getAgentClassString() {
		return "O";
	}

	@Override
	public void perform(Argument[] args, Context context)
			throws ExtensionException, LogoException {
		GSExtension.clear();
	}
}
