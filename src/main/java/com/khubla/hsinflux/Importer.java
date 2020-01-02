package com.khubla.hsinflux;

import java.io.*;
import java.util.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;
import com.khubla.hsclient.response.*;

public class Importer {
	public String getHsURL() {
		return hsURL;
	}

	public String getHsPassword() {
		return hsPassword;
	}

	public String getHsUsername() {
		return hsUsername;
	}

	private final String hsURL;
	private final String hsPassword;
	private final String hsUsername;

	public Importer(String hsURL, String hsPassword, String hsUsername) {
		super();
		this.hsURL = hsURL;
		this.hsPassword = hsPassword;
		this.hsUsername = hsUsername;
	}

	public void run() throws HSClientException, InterruptedException, IOException {
		Device device = getDevice("Furnace Thermostat", "Z-Wave Temperature");
		if (device != null) {
			File f = new File("output.txt");
			if (false == f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream("output.txt", true);
			while (true) {
				String status = getStatus(device);
				String s = new Date().getTime() + "," + status + "\n";
				fos.write(s.getBytes());
				fos.flush();
				System.out.print(s);
				Thread.sleep(1000 * 60);
			}
		}
	}

	public String getStatus(Device device) throws HSClientException {
		HSClient hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
		StatusResponse statusResponse = hsClient.getStatus(device.getRef(), null, null);
		return (statusResponse.getDevices().get(0).getStatus());
	}

	public Device getDevice(String parentDeviceName, String subDeviceType) throws HSClientException {
		HSClient hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
		Map<String, Device> deviceMapByName = hsClient.getDevicesByName();
		Map<Integer, Device> deviceMapByRef = hsClient.getDevicesByRef();
		Device mainFloorThermostat = deviceMapByName.get(parentDeviceName);
		for (Integer ref : mainFloorThermostat.getAssociated_devices()) {
			Device associatedDevice = deviceMapByRef.get(ref);
			if (subDeviceType.compareTo(associatedDevice.getDevice_type_string()) == 0) {
				return associatedDevice;
			}
		}
		return null;
	}
}
