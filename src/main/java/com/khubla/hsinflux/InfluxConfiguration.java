package com.khubla.hsinflux;

/**
 * @author Tom Everett
 *         <p>
 *         Copyright (C) 2020,tom@khubla.com
 *         </p>
 */
public class InfluxConfiguration {
	private final String influxurl;
	private final String influxuser;
	private final String influxpassword;
	private final String influxdb;

	public InfluxConfiguration(String influxurl, String influxuser, String influxpassword, String influxdb) {
		super();
		this.influxurl = influxurl;
		this.influxuser = influxuser;
		this.influxpassword = influxpassword;
		this.influxdb = influxdb;
	}

	public String getInfluxdb() {
		return influxdb;
	}

	public String getInfluxpassword() {
		return influxpassword;
	}

	public String getInfluxurl() {
		return influxurl;
	}

	public String getInfluxuser() {
		return influxuser;
	}
}
