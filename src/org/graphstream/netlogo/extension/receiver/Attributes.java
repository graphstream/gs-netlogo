package org.graphstream.netlogo.extension.receiver;

import java.util.HashMap;
import java.util.Map;

import org.nlogo.api.LogoList;

public class Attributes {
	Map<String, LogoList> map;

	public Attributes() {
		map = new HashMap<String, LogoList>();
	}

	public LogoList get(String attribute) {
		LogoList list = map.remove(attribute);
		if (list == null)
			list = new LogoList();
		return list;
	}

	public void add(String attribute, Object value) {
		Object logoValue = netStreamToLogo(value);
		if (logoValue == null)
			return;
		LogoList list = map.get(attribute);
		if (list == null) {
			list = new LogoList();
			map.put(attribute, list);
		}
		list.add(logoValue);
	}

	public int size() {
		return map.size();
	}

	protected static Object netStreamToLogo(Object o) {
		Object result = simpleNetStreamToLogo(o);
		if (result != null)
			return result;
		if (!o.getClass().isArray())
			return null;
		LogoList list = new LogoList();
		for (Object element : (Object[]) o) {
			Object logoElement = simpleNetStreamToLogo(element);
			if (logoElement == null) {
				list.clear();
				return null;
			}
			list.add(logoElement);
		}
		return list;
	}

	protected static Object simpleNetStreamToLogo(Object o) {
		if (o instanceof Boolean || o instanceof String || o instanceof Double)
			return o;
		if (o instanceof Number)
			return new Double(((Number) o).doubleValue());
		return null;
	}
}
