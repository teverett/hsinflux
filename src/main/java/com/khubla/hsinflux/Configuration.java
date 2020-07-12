package com.khubla.hsinflux;

import java.io.*;
import java.util.*;

import com.khubla.hsclient.*;

/**
 * @author Tom Everett
 *         <p>
 *         Copyright (C) 2020,tom@khubla.com
 *         </p>
 */
public class Configuration {
	private static final String FILENAME = "hsinflux.properties";
	private static Configuration instance = null;

	public static Configuration getInstance() {
		if (null == instance) {
			instance = new Configuration();
		}
		return instance;
	}

	private HSConfiguration hsConfiguration;
	private InfluxConfiguration influxConfiguration;
	private int pollingthreads;
	private int pollinginterval;

	private Configuration() {
		load();
	}

	public HSConfiguration getHsConfiguration() {
		return hsConfiguration;
	}

	public InfluxConfiguration getInfluxConfiguration() {
		return influxConfiguration;
	}

	public int getPollinginterval() {
		return pollinginterval;
	}

	public int getPollingthreads() {
		return pollingthreads;
	}

	private void load() {
		try {
			final Properties properties = new Properties();
			properties.load(new FileInputStream(FILENAME));
			hsConfiguration = new HSConfiguration(properties.getProperty("hsurl"), properties.getProperty("hsuser"), properties.getProperty("hspassword"));
			influxConfiguration = new InfluxConfiguration(properties.getProperty("influxurl"), properties.getProperty("influxuser"), properties.getProperty("influxpassword"),
					properties.getProperty("influxdb"));
			pollingthreads = Integer.parseInt(properties.getProperty("pollingthreads"));
			pollinginterval = Integer.parseInt(properties.getProperty("pollinginterval"));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
