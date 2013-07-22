package org.graphstream.netlogo.extension.command;

import java.util.HashMap;

import org.graphstream.netlogo.extension.GSExtension;
import org.graphstream.netlogo.extension.GraphProcess;
import org.graphstream.netlogo.extension.exec.Betweenness;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

public class ProcessCommand {
	private static final HashMap<String, GraphProcess> PROCESS = new HashMap<String, GraphProcess>();

	public static void register(String name, GraphProcess process) {
		PROCESS.put(name, process);
	}

	static {
		register("betweenness", new Betweenness());
	}

	public static class Init extends DefaultCommand {
		@Override
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(new int[] { Syntax.StringType() },
					Syntax.ListType());
		}

		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {
			GraphProcess process = PROCESS.get(arg0[0].getString());

			if (process != null) {
				Argument[] args = new Argument[arg0.length - 1];
				System.arraycopy(arg0, 1, args, 0, args.length);
				GSExtension.add(process);
				process.init(args);
			} else {
				// TODO
			}
		}
	}

	public static class Set extends DefaultCommand {
		@Override
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(new int[] { Syntax.StringType() },
					Syntax.ListType());
		}

		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {

		}
	}

	public static class Exec extends DefaultCommand {
		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {
			GraphProcess process = PROCESS.get(arg0[0].getString());

			if (process != null) {
				process.exec();
			} else {
				// TODO
			}
		}
	}

	public static class Terminate extends DefaultCommand {
		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {
			GraphProcess process = PROCESS.get(arg0[0].getString());

			if (process != null) {
				process.end();
			} else {
				// TODO
			}
		}
	}
}
