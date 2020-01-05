package com.khubla.hsinflux.pointgenerator;

import java.util.concurrent.*;

import org.influxdb.dto.*;

import com.khubla.hsclient.domain.*;
import com.khubla.hsinflux.*;

/**
 * @author Tom Everett.
 * @copyright Copyright (C) 2020,tom@khubla.com
 */
public class DevicePointGeneratorImpl implements PointGenerator<Device> {
	private static final String MEASUREMENT_NAME = "device";

	@Override
	public Point generatePoint(Device device) {
		try {
			final Double value = device.getValue();
			final long lastChange = device.getLast_change() == null ? 0 : device.getLast_change().getTime();
			final Point point = Point.measurement(MEASUREMENT_NAME).tag("ref", device.getRef().toString()).tag("location1", device.getLocation()).tag("location1", device.getLocation())
					.tag("location2", device.getLocation2()).tag("name", getDeviceName(device)).tag("type", device.getDevice_type_string()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("value", value).addField("status", device.getStatus()).addField("lastchange", lastChange).build();
			return point;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
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
}
