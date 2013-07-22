package org.graphstream.netlogo.extension.exec;

import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.netlogo.extension.GSExtension;
import org.graphstream.netlogo.extension.GraphProcess;
import org.nlogo.api.Argument;

public class Betweenness implements GraphProcess {
	BetweennessCentrality bc;

	public Betweenness() {
		bc = new BetweennessCentrality();
	}

	public void init(Argument[] args) {
		bc.setCentralityAttributeName("centrality");
		bc.init(GSExtension.getGraph());
	}

	public void set(String key, Object value) {

	}

	public void exec() {
		double max = -1;
		bc.compute();

		for (int i = 0; i < GSExtension.getGraph().getNodeCount(); i++)
			max = Math.max(max,
					bc.centrality(GSExtension.getGraph().getNode(i)));

		GSExtension.getGraph().setAttribute("MAX-CENTRALITY", max);
	}

	public void end() {

	}

	public boolean runAtTick() {
		return true;
	}

}
