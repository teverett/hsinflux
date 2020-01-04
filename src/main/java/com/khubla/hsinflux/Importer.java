package com.khubla.hsinflux;

import java.io.*;
import java.util.*;

import org.influxdb.*;
import org.influxdb.dto.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;
import com.khubla.hsclient.response.*;

public class Importer {
	private static final String DB_NAME = "house";
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
		Map<Integer, Device> devices = null;
		HSClient hsClient = null;
		try {
			/*
			 * get devices
			 */
			hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
			devices = hsClient.getDevicesByRef();
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
		if (devices != null) {
			while (true) {
				try {
					/*
					 * walk devices
					 */
					final BatchPoints batchPoints = BatchPoints.database(DB_NAME).build();
					for (final Integer ref : devices.keySet()) {
						final Device device = updateDevice(ref);
						final PointGenerator<Device> pointGenerator = new GenericPointGeneratorImpl();
						final Point point = pointGenerator.generatePoint(device);
						batchPoints.point(point);
					}
					writeToInflux(batchPoints);
					Thread.sleep(1000 * 60);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Device updateDevice(Integer ref) throws HSClientException, IOException {
		HSClient hsClient = null;
		try {
			hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
			final StatusResponse statusResponse = hsClient.getStatus(ref, null, null);
			return (statusResponse.getDevices().get(0));
		} finally {
			hsClient.close();
		}
	}

	private void writeToInflux(BatchPoints batchPoints) {
		InfluxDB influxDB = null;
		try {
			influxDB = InfluxDBFactory.connect(influxURL, influxUsername, influxPassword);
			influxDB.write(batchPoints);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			influxDB.close();
		}
	}
}
