package org.graphstream.netlogo.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.Sink;
import org.nlogo.agent.Link;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.agent.AgentSet;
import org.nlogo.api.Agent;
import org.nlogo.api.AgentException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoList;
import org.nlogo.api.LogoListBuilder;

public class NetLogoSink implements Sink {
	World world;

	HashMap<String, Turtle> nodes2turtles;
	HashMap<Turtle, String> turtles2nodes;
	HashMap<String, Link> edges2links;
	HashMap<Link, String> links2edges;
	HashSet<String> skipAttributes;

	public NetLogoSink(World world) {
		this.world = world;

		this.nodes2turtles = new HashMap<String, Turtle>();
		this.turtles2nodes = new HashMap<Turtle, String>();
		this.edges2links = new HashMap<String, Link>();
		this.links2edges = new HashMap<Link, String>();

		this.skipAttributes = new HashSet<String>();
	}

	protected AgentSet getBreed(String nodeId) {
		int idx = nodeId.indexOf('.');
		AgentSet breed = null;

		if (idx >= 0) {
			String breedName = nodeId.substring(0, idx).toUpperCase();
			breed = world.getBreed(breedName);
		}

		if (breed == null)
			breed = world.turtles();

		if (breed == null)
			System.err.printf("unknown breed for node \"%s\"\n", nodeId);

		return breed;
	}

	protected Turtle getTurtle(String nodeId) {
		return nodes2turtles.get(nodeId);
	}

	protected AgentSet getLinkBreed(String edgeId) {
		int idx = edgeId.indexOf('.');
		AgentSet breed = null;

		if (idx >= 0)
			breed = world.getBreed(edgeId.substring(0, idx));

		if (breed == null)
			breed = world.links();

		return breed;
	}

	protected Link getLink(String edgeId) {
		return edges2links.get(edgeId);
	}

	protected Object getNetLogoValue(Object value) {
		Object logoValue = getNetLogoSimpleValue(value);

		if (logoValue != null)
			return logoValue;

		if (!value.getClass().isArray())
			return null;

		LogoListBuilder builder = new LogoListBuilder();

		for (Object element : (Object[]) value) {
			Object logoElement = getNetLogoSimpleValue(element);

			if (logoElement == null)
				return null;

			builder.add(logoElement);
		}

		return builder.toLogoList();
	}

	protected Object getNetLogoSimpleValue(Object value) {
		if (value instanceof Boolean || value instanceof String
				|| value instanceof Double)
			return value;

		if (value instanceof Number)
			return Double.valueOf(((Number) value).doubleValue());

		return null;
	}

	protected void setTurtleAttribute(String nodeId, String attribute,
			Object value) {
		attribute = attribute.toUpperCase();

		AgentSet breed = getBreed(nodeId);
		int idx = world.breedsOwnIndexOf(breed, attribute);

		if (idx < 0)
			idx = world.turtlesOwnIndexOf(attribute);

		if (idx >= 0) {
			Turtle t = getTurtle(nodeId);

			try {
				t.setVariable(idx, getNetLogoValue(value));
			} catch (AgentException e) {
				e.printStackTrace();
			}
		} else {
			if (!skipAttributes.contains(attribute)) {
				System.err.printf("skip attribute \"%s\"\n", attribute);
				skipAttributes.add(attribute);
			}
		}
	}

	Node checkTurtle(Turtle t) {
		String id = null;
		Node node = null;

		if (turtles2nodes.containsKey(t))
			id = turtles2nodes.get(t);
		else {
			id = t.getBreed().printName() + "." + Long.toString(t.id());
			turtles2nodes.put(t, id);

			if (nodes2turtles.containsKey(id) && nodes2turtles.get(id) != t)
				throw new RuntimeException("???");

			nodes2turtles.put(id, t);
		}

		node = GSExtension.getGraph().getNode(id);

		if (node == null)
			node = GSExtension.getGraph().addNode(id);

		return node;
	}

	Edge checkLink(Link l) {
		String id = null;
		Edge edge = null;

		if (links2edges.containsKey(l))
			id = links2edges.get(l);
		else {
			id = l.getBreed().printName() + "." + Long.toString(l.id());
			links2edges.put(l, id);

			if (edges2links.containsKey(id) && edges2links.get(id) != l)
				throw new RuntimeException("???");

			edges2links.put(id, l);
		}

		edge = GSExtension.getGraph().getEdge(id);

		if (edge == null)
			edge = GSExtension.getGraph().addEdge(id,
					turtles2nodes.get(l.end1()), turtles2nodes.get(l.end2()),
					l.isDirectedLink());

		return edge;
	}

	void checkAttributes(Node n, Turtle t) {
		double[] xyz = n.getAttribute("xyz");

		if (xyz == null)
			xyz = new double[2];

		xyz[0] = t.xcor();
		xyz[1] = t.ycor();

		n.setAttribute("xyz", xyz);
	}

	void checkAttributes(Edge e, Link l) {

	}

	void check(World w) {
		Graph g = GSExtension.getGraph();
		LinkedList<String> tmp = new LinkedList<String>();

		g.removeSink(this);

		for (Agent a : world.turtles().agents()) {
			Turtle t = (Turtle) a;
			Node n = checkTurtle(t);

			checkAttributes(n, t);
		}

		for (String id : nodes2turtles.keySet()) {
			Turtle t = nodes2turtles.get(id);

			if (!world.turtles().contains(t))
				tmp.add(id);
		}

		for (int i = 0; i < tmp.size(); i++) {
			String id = tmp.get(i);

			g.removeNode(id);
			turtles2nodes.remove(nodes2turtles.remove(id));
		}

		tmp.clear();

		for (Agent a : world.links().agents()) {
			Link l = (Link) a;
			Edge e = checkLink(l);

			checkAttributes(e, l);
		}

		for (String id : edges2links.keySet()) {
			Link l = edges2links.get(id);

			if (!world.links().contains(l))
				tmp.add(id);
		}

		for (int i = 0; i < tmp.size(); i++) {
			String id = tmp.get(i);

			g.removeEdge(id);
			links2edges.remove(edges2links.remove(id));
		}

		tmp.clear();
		g.addSink(this);
	}

	protected void setGlobalVariable(String key, Object value) {
		int idx = world.observerOwnsIndexOf(key.toUpperCase());

		if (idx < 0) {
			if (!skipAttributes.contains(key)) {
				System.err.printf("Skip global attribute \"%s\"\n", key);
				skipAttributes.add(key);
			}

			return;
		}

		try {
			world.observer().setVariable(idx, getNetLogoValue(value));
		} catch (AgentException e) {
			e.printStackTrace();
		} catch (LogoException e) {
			e.printStackTrace();
		}
	}

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		setGlobalVariable(attribute, value);
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		setGlobalVariable(attribute, newValue);
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		setTurtleAttribute(nodeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		setTurtleAttribute(nodeId, attribute, newValue);
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		setTurtleAttribute(nodeId, attribute, LogoList.Empty());
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		// TODO Auto-generated method stub

	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		if (nodes2turtles.containsKey(nodeId))
			return;

		AgentSet set = getBreed(nodeId);
		Turtle t = world.createTurtle(set);

		nodes2turtles.put(nodeId, t);
		turtles2nodes.put(t, nodeId);
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		Turtle t = getTurtle(nodeId);

		if (t != null) {
			t.die();
			nodes2turtles.remove(nodeId);
			turtles2nodes.remove(t);
		}
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		Turtle e1 = nodes2turtles.get(fromNodeId);
		Turtle e2 = nodes2turtles.get(toNodeId);

		if (e1 == null || e2 == null) {
			System.err.printf("link extremity missing\n");
			return;
		}

		if (edges2links.containsKey(edgeId)) {
			System.err.printf("Link already exists\n");
			return;
		}

		AgentSet breed = getLinkBreed(edgeId);

		Link l = world.linkManager.createLink(e1, e2, breed);
		edges2links.put(edgeId, l);
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		Link l = getLink(edgeId);

		if (l != null) {
			l.die();
			edges2links.remove(edgeId);
		}
	}

	public void graphCleared(String sourceId, long timeId) {
		// TODO Auto-generated method stub

	}

	public void stepBegins(String sourceId, long timeId, double step) {
		// TODO Auto-generated method stub

	}

}
