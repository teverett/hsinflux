package com.khubla.hsinflux;

/**
 * @author Tom Everett
 *         <p>
 *         Copyright (C) 2020,tom@khubla.com
 *         </p>
 */
public class Main {
	public static void main(String... args) {
		System.out.println("HomeSeer Influx Importer");
		try {
			/*
			 * go
			 */
			final Configuration configuration = Configuration.getInstance();
			final Importer importer = new Importer(configuration);
			importer.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}