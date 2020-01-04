package com.khubla.hsinflux;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.influxdb.*;
import org.influxdb.dto.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;
import com.khubla.hsclient.response.*;
import com.khubla.hsclient.util.*;

public class Importer {
	private final String hsURL;
	private final String hsPassword;
	private final String hsUsername;
	private final String influxURL;
	private final String influxPassword;
	private final String influxUsername;

	public Importer(String hsURL, String hsUsername, String hsPassword, String influxURL, String influxUsername, String influxPassword) {
		super();
		this.hsURL = hsURL;
		this.hsPassword = hsPassword;
		this.hsUsername = hsUsername;
		this.influxURL = influxURL;
		this.influxUsername = influxUsername;
		this.influxPassword = influxPassword;
	}

	private String getDeviceName(Device device) {
		final StringBuilder stringBuilder = new StringBuilder();
		final String n = device.getName();
		for (int i = 0; i < n.length(); i++) {
			if ((((n.charAt(i) >= 'a') && (n.charAt(i) <= 'z'))) || (((n.charAt(i) >= 'A') && (n.charAt(i) <= 'Z')))) {
				stringBuilder.append(n.charAt(i));
			}
		}
		return stringBuilder.toString();
	}

	private double getDeviceTemperature(Device device) {
		String status = device.getStatus();
		final int i = status.indexOf('C');
		if (-1 != i) {
			status = status.substring(0, i - 1);
		}
		return Double.parseDouble(status.trim());
	}

	public String getHsPassword() {
		return hsPassword;
	}

	public String getHsURL() {
		return hsURL;
	}

	public String getHsUsername() {
		return hsUsername;
	}

	public String getInfluxPassword() {
		return influxPassword;
	}

	public String getInfluxURL() {
		return influxURL;
	}

	public String getInfluxUsername() {
		return influxUsername;
	}

	public String getStatus(Device device) throws HSClientException {
		final HSClient hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
		final StatusResponse statusResponse = hsClient.getStatus(device.getRef(), null, null);
		return (statusResponse.getDevices().get(0).getStatus());
	}

	public void run() throws HSClientException, InterruptedException, IOException {
		/*
		 * get devices
		 */
		final HSClient hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
		final DeviceUtil deviceUtil = new DeviceUtil(hsClient);
		final List<Device> devices = deviceUtil.getDevices("Z-Wave Temperature");
		/*
		 * spin
		 */
		if (devices != null) {
			while (true) {
				for (final Device device : devices) {
					writeToInflux(getDeviceName(device), getDeviceTemperature(device));
				}
				Thread.sleep(1000 * 60);
			}
		}
	}

	private void writeToInflux(String name, double temperature) {
		InfluxDB influxDB = null;
		try {
			influxDB = InfluxDBFactory.connect(influxURL, influxUsername, influxPassword);
			influxDB.setDatabase("house");
			final Point point = Point.measurement("temperature").tag("name", name).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("temperature", temperature).build();
			influxDB.write(point);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			influxDB.close();
		}
	}
}
