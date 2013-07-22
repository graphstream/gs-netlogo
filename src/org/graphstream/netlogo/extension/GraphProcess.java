package org.graphstream.netlogo.extension;

import org.nlogo.api.Argument;

public interface GraphProcess {
	void init(Argument[] args);

	void set(String key, Object value);
	
	void exec();

	void end();
	
	boolean runAtTick();
}
