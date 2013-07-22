package org.graphstream.netlogo.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import scala.collection.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.netlogo.extension.command.Generate;
import org.graphstream.netlogo.extension.command.ProcessCommand;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.swingViewer.Viewer;
import org.nlogo.agent.Link;
import org.nlogo.agent.Observer;
import org.nlogo.agent.Turtle;
import org.nlogo.api.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.CompilerException;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.Context;
import org.nlogo.api.ValueConstraint;
import org.nlogo.api.World;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.widget.SwitchWidget;
import org.nlogo.window.GUIWorkspace;
import org.nlogo.window.InputBoxWidget;
import org.nlogo.window.SliderWidget;
import org.nlogo.window.ViewManager;
import org.nlogo.window.Widget;
import org.nlogo.window.WidgetRegistry;
import org.nlogo.api.NetLogoListener;
import org.nlogo.app.App;
import org.nlogo.app.Tabs;
import org.nlogo.app.WidgetPanel;

/**
 * This is the main extension class.
 * 
 * It maintains a common sync mechanism for all senders and receivers, as well
 * as methods to access senders and receivers by their id, to add and to remove
 * them.
 * 
 * @author Stefan Balev
 * @author Guilhelm Savin
 * 
 */
public class GSExtension extends DefaultClassManager implements NetLogoListener {
	public static Graph getGraph() {
		return g;
	}

	static Graph g;
	Viewer v;
	ExtensionContext ctx;
	NetLogoSink sink;
	int tabIndex = -1;
	ThreadProxyPipe pipe;
	static LinkedList<GraphProcess> process = new LinkedList<GraphProcess>();

	@Override
	public void load(PrimitiveManager manager) throws ExtensionException {
		g = null;

		manager.addPrimitive("init", new InitGraphStream());
		manager.addPrimitive("show", new DisplayCommand());
		manager.addPrimitive("release", new ReleaseGraphStream());
		manager.addPrimitive("generate", new Generate());

		manager.addPrimitive("process-init", new ProcessCommand.Init());
		manager.addPrimitive("process-set", new ProcessCommand.Set());
		manager.addPrimitive("process-exec", new ProcessCommand.Exec());
		manager.addPrimitive("process-terminate",
				new ProcessCommand.Terminate());
	}

	public void init(ExtensionContext ctx) {
		if (this.ctx != null) {
			GUIWorkspace workspace = (GUIWorkspace) this.ctx.workspace();
			workspace.listenerManager.removeListener(this);
		}

		for (int i = 0; i < process.size(); i++)
			process.get(i).end();

		process.clear();

		this.ctx = ctx;

		GUIWorkspace workspace = (GUIWorkspace) ctx.workspace();
		workspace.listenerManager.addListener(this);

		g = new AdjacencyListGraph("netlogo");
		sink = new NetLogoSink(ctx.workspace().world());

		g.addSink(sink);

		putGlobalVariables();
	}

	protected void putGlobalVariables() {
		org.nlogo.agent.World w = ctx.workspace().world();
		org.nlogo.agent.Observer ob = w.observer();

		for (int i = 0; i < ob.getVariableCount(); i++) {
			String key = w.observerOwnsNameAt(i).toLowerCase();
			Object value = ob.getVariable(i);

			System.out.printf("%s = %s\n", key, value);
			g.addAttribute(key, value);
		}
	}

	public static void add(GraphProcess process) {
		GSExtension.process.add(process);
	}

	class InitGraphStream extends DefaultCommand {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.nlogo.api.Command#perform(org.nlogo.api.Argument[],
		 * org.nlogo.api.Context)
		 */
		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {
			init((ExtensionContext) arg1);
		}
	}

	class ReleaseGraphStream extends DefaultCommand {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.nlogo.api.Command#perform(org.nlogo.api.Argument[],
		 * org.nlogo.api.Context)
		 */
		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {
			GUIWorkspace workspace = (GUIWorkspace) ctx.workspace();
			workspace.listenerManager.removeListener(GSExtension.this);

			if (v != null)
				v.close();

			v = null;
			g = null;
		}
	}

	class DisplayCommand extends DefaultCommand {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.nlogo.api.Command#perform(org.nlogo.api.Argument[],
		 * org.nlogo.api.Context)
		 */
		public void perform(Argument[] arg0, Context arg1)
				throws ExtensionException, LogoException {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (v != null)
						v.close();
					World w = ctx.workspace().world();
					v = new Viewer(g,
							Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

					v.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
					v.addDefaultView(false);
					
					v.getDefaultView()
							.getCamera()
							.setGraphViewport(w.minPxcor(), w.minPycor(),
									w.maxPxcor(), w.maxPycor());
					
					Tabs tabs = App.app().tabs();

					if (tabIndex >= 0)
						tabs.removeTabAt(tabIndex);

					tabIndex = tabs.getTabCount();
					tabs.addTab("GraphStream", v.getDefaultView());
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#tickCounterChanged(double)
	 */
	public void tickCounterChanged(double arg0) {
		if (!(ctx.getAgent() instanceof Observer))
			return;

		sink.check(ctx.workspace().world());

		// HashSet<String> nodes = new HashSet<String>();
		// HashMap<String, Link> links = new HashMap<String, Link>();
		//
		// for (Agent a : world.turtles().agents()) {
		// Turtle t = (Turtle) a;
		// String id = Long.toString(a.id());
		// nodes.add(id);
		//
		// Node n = g.getNode(id);
		//
		// if (n == null)
		// n = g.addNode(id);
		//
		// double[] xyz = n.getAttribute("xyz");
		//
		// if (xyz == null)
		// xyz = new double[2];
		//
		// xyz[0] = t.xcor();
		// xyz[1] = t.ycor();
		//
		// n.setAttribute("xyz", xyz);
		// }
		//
		// for (int i = 0; i < g.getNodeCount(); i++)
		// if (!nodes.contains(g.getNode(i).getId()))
		// g.removeNode(g.getNode(i));
		//
		// for (Agent a : world.links().agents()) {
		// Link l = (Link) a;
		// String id = Long.toString(l.id());
		// links.put(id, l);
		// }
		//
		// for (int i = 0; i < g.getEdgeCount(); i++)
		// if (!links.containsKey(g.getEdge(i).getId()))
		// g.removeEdge(g.getEdge(i));
		//
		// for (String id : links.keySet())
		// if (g.getEdge(id) == null) {
		// Link l = links.get(id);
		//
		// g.addEdge(id, Long.toString(l.end1().id()),
		// Long.toString(l.end2().id()), l.isDirectedLink());
		// }

		for (GraphProcess p : process)
			if (p.runAtTick())
				p.exec();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#buttonPressed(java.lang.String)
	 */
	public void buttonPressed(String arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#buttonStopped(java.lang.String)
	 */
	public void buttonStopped(String arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#chooserChanged(java.lang.String,
	 * java.lang.Object, boolean)
	 */
	public void chooserChanged(String arg0, Object arg1, boolean arg2) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#codeTabCompiled(java.lang.String,
	 * org.nlogo.api.CompilerException)
	 */
	public void codeTabCompiled(String arg0, CompilerException arg1) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#commandEntered(java.lang.String,
	 * java.lang.String, char, org.nlogo.api.CompilerException)
	 */
	public void commandEntered(String arg0, String arg1, char arg2,
			CompilerException arg3) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#inputBoxChanged(java.lang.String,
	 * java.lang.Object, boolean)
	 */
	public void inputBoxChanged(String arg0, Object arg1, boolean arg2) {
		System.out.printf("[%s] %s %s\n", arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#modelOpened(java.lang.String)
	 */
	public void modelOpened(String arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#possibleViewUpdate()
	 */
	public void possibleViewUpdate() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#sliderChanged(java.lang.String,
	 * double, double, double, double, boolean, boolean)
	 */
	public void sliderChanged(String arg0, double arg1, double arg2,
			double arg3, double arg4, boolean arg5, boolean arg6) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nlogo.api.NetLogoListener#switchChanged(java.lang.String,
	 * boolean, boolean)
	 */
	public void switchChanged(String arg0, boolean arg1, boolean arg2) {
	}
}
