package com.khubla.hsinflux;

import java.io.*;
import java.util.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;
import com.khubla.hsclient.response.*;
import com.khubla.hsclient.util.*;

public class Importer {
	private static final String OUTPUTFILE = "house.txt";
	private final String hsURL;
	private final String hsPassword;
	private final String hsUsername;

	public Importer(String hsURL, String hsPassword, String hsUsername) {
		super();
		this.hsURL = hsURL;
		this.hsPassword = hsPassword;
		this.hsUsername = hsUsername;
	}

	private void createFile() throws IOException {
		final File f = new File(OUTPUTFILE);
		if (false == f.exists()) {
			f.createNewFile();
			/*
			 * write header
			 */
			FileOutputStream fos = null;
			PrintWriter printWriter = null;
			try {
				fos = new FileOutputStream(OUTPUTFILE, true);
				printWriter = new PrintWriter(fos);
				printWriter.write("# DDL\n");
				printWriter.write("CREATE DATABASE house\n");
				printWriter.write("CREATE RETENTION POLICY oneyear ON house DURATION 52w REPLICATION 1\n");
				printWriter.write("\n");
				printWriter.write("# DML\n");
				printWriter.write("# CONTEXT-DATABASE: house\n");
				printWriter.write("# CONTEXT-RETENTION-POLICY: oneyear\n");
			} finally {
				printWriter.flush();
				printWriter.close();
				fos.close();
			}
		}
	}

	private String createStatusLine(Device device) throws HSClientException {
		/*
		 * tags
		 */
		final Map<String, String> tags = new HashMap<String, String>();
		tags.put("devicename", getDeviceName(device));
		/*
		 * fields
		 */
		final Map<String, String> fields = new HashMap<String, String>();
		fields.put("temperature", getDeviceTemperature(device));
		/*
		 * line
		 */
		return LineProtocol.line("housetemperature", tags, fields, Long.toString(System.currentTimeMillis()));
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

	private String getDeviceTemperature(Device device) {
		String status = device.getStatus();
		final int i = status.indexOf('C');
		if (-1 != i) {
			status = status.substring(0, i - 1);
		}
		return status.trim();
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

	public String getStatus(Device device) throws HSClientException {
		final HSClient hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
		final StatusResponse statusResponse = hsClient.getStatus(device.getRef(), null, null);
		return (statusResponse.getDevices().get(0).getStatus());
	}

	public void run() throws HSClientException, InterruptedException, IOException {
		/*
		 * create file
		 */
		createFile();
		/*
		 * open stream
		 */
		FileOutputStream fos = null;
		PrintWriter printWriter = null;
		try {
			fos = new FileOutputStream(OUTPUTFILE, true);
			printWriter = new PrintWriter(fos);
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
						final String s = createStatusLine(device) + "\n";
						printWriter.write(s);
						printWriter.flush();
					}
					Thread.sleep(1000 * 60);
				}
			}
		} finally {
			printWriter.close();
			fos.close();
		}
	}
}
