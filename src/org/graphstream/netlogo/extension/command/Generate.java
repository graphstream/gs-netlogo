package org.graphstream.netlogo.extension.command;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.netlogo.extension.GSExtension;
import org.nlogo.agent.Observer;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

public class Generate extends DefaultCommand {

	public void perform(Argument[] arg0, Context arg1)
			throws ExtensionException, LogoException {
		if (!(arg1.getAgent() instanceof Observer))
			return;
		
		BarabasiAlbertGenerator gen = new BarabasiAlbertGenerator();
		gen.addNodeAttribute("test");

		gen.addSink(GSExtension.getGraph());
		gen.begin();
		for (int i = 0; i < 100; i++)
			gen.nextEvents();
		gen.end();

		gen.removeSink(GSExtension.getGraph());
	}

}
