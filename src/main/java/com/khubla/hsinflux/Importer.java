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

	private double getDeviceTemperature(Device device) throws HSClientException, IOException {
		String status = updateCurrentStatus(device);
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

	public void run() throws HSClientException, InterruptedException, IOException {
		List<Device> temperatureDevices = null;
		List<Device> heatingSetpointDevices = null;
		List<Device> coolingSetpointDevices = null;
		HSClient hsClient = null;
		try {
			/*
			 * get devices
			 */
			hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
			final DeviceUtil deviceUtil = new DeviceUtil(hsClient);
			temperatureDevices = deviceUtil.getDevices("Z-Wave Temperature");
			heatingSetpointDevices = deviceUtil.getDevices("Z-Wave Heating  Setpoint");
			coolingSetpointDevices = deviceUtil.getDevices("Z-Wave Cooling  Setpoint");
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (null != hsClient) {
				hsClient.close();
			}
		}
		/*
		 * spin
		 */
		if (temperatureDevices != null) {
			while (true) {
				try {
					for (final Device device : temperatureDevices) {
						writeToInflux(getDeviceName(device), "temperature", getDeviceTemperature(device));
					}
					for (final Device device : heatingSetpointDevices) {
						writeToInflux(getDeviceName(device), "thermostatHeat", getDeviceTemperature(device));
					}
					for (final Device device : coolingSetpointDevices) {
						writeToInflux(getDeviceName(device), "thermostatCool", getDeviceTemperature(device));
					}
					Thread.sleep(1000 * 60);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String updateCurrentStatus(Device device) throws HSClientException, IOException {
		HSClient hsClient = null;
		try {
			hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
			final StatusResponse statusResponse = hsClient.getStatus(device.getRef(), null, null);
			return (statusResponse.getDevices().get(0).getStatus());
		} finally {
			hsClient.close();
		}
	}

	private void writeToInflux(String name, String type, double temperature) {
		InfluxDB influxDB = null;
		try {
			influxDB = InfluxDBFactory.connect(influxURL, influxUsername, influxPassword);
			influxDB.setDatabase("house");
			final Point point = Point.measurement("temperature").tag("name", name).tag("type", type).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("temperature", temperature).build();
			influxDB.write(point);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			influxDB.close();
		}
	}
}
