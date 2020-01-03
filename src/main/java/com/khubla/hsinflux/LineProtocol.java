package com.khubla.hsinflux;

import java.util.*;

/**
 * @author tom
 */
public class LineProtocol {
	public static String line(String measure, Map<String, String> tags, Map<String, String> fields, String timestamp) {
		String ret = measure + ",";
		if (null != tags) {
			ret += toList(tags);
			ret += " ";
		}
		ret += toList(fields);
		if (null != timestamp) {
			ret += " " + timestamp;
		}
		return ret;
	}

	private static String toList(Map<String, String> list) {
		String ret = "";
		boolean first = true;
		for (final String key : list.keySet()) {
			if (true == first) {
				first = false;
			} else {
				ret += ",";
			}
			ret += key + "=" + list.get(key);
		}
		return ret;
	}
}
