package com.khubla.hsinflux;

import java.util.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;

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

	public void run() throws HSClientException {
		HSClient hsClient = HSClientImpl.connect(hsURL, hsUsername, hsPassword);
		Map<String, Device> deviceMapByName = hsClient.getDevicesByName();
		Map<Integer, Device> deviceMapByRef = hsClient.getDevicesByRef();
		for (String name : deviceMapByName.keySet()) {
			System.out.println(name);
		}
		Device mainFloorThermostat = deviceMapByName.get("Furnace Thermostat");
		System.out.println(Integer.toString(mainFloorThermostat.getRef()));
		for (Integer ref : mainFloorThermostat.getAssociated_devices()) {
			Device associatedDevice = deviceMapByRef.get(ref);
			System.out.println(associatedDevice.getDevice_type_string());
		}
	}
}
