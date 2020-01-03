package com.khubla.hsinflux;

import java.util.*;

import org.junit.*;

public class TestLineProtocol {
	@Test
	public void test1() {
		Map<String, String> fields = new HashMap<String, String>();
		Map<String, String> tags = new HashMap<String, String>();
		String measure = "weather";
		String timestamp = "1465839830100400200";
		tags.put("season", "summer");
		tags.put("location", "us-midwest");
		fields.put("temperature", "82");
		String lp = LineProtocol.line(measure, tags, fields, timestamp);
		String r = "weather,season=summer,location=us-midwest temperature=82 1465839830100400200";
		// System.out.println(lp);
		// System.out.println(r);
		Assert.assertTrue(lp.compareTo(r) == 0);
	}
}
