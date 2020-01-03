package com.khubla.hsinflux;

import java.io.*;
import java.util.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;
import com.khubla.hsclient.response.*;
import com.khubla.hsclient.util.*;

public class Importer {
	private final String hsURL;
	private final String hsPassword;
	private final String hsUsername;

	public Importer(String hsURL, String hsPassword, String hsUsername) {
		super();
		this.hsURL = hsURL;
		this.hsPassword = hsPassword;
		this.hsUsername = hsUsername;
	}

	private String createStatusLine(Device device) throws HSClientException {
		final Map<String, String> fields = new HashMap<String, String>();
		fields.put("status", device.getStatus());
		return LineProtocol.line(device.getName(), null, fields, null);
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
		final File f = new File("output.txt");
		if (false == f.exists()) {
			f.createNewFile();
		}
		final FileOutputStream fos = new FileOutputStream("output.txt", true);
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
					fos.write(s.getBytes());
					fos.flush();
					System.out.print(s);
				}
				Thread.sleep(1000 * 60);
			}
		}
	}
}
