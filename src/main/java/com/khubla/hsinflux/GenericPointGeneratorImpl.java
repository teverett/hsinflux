package com.khubla.hsinflux;

import java.util.concurrent.*;

import org.influxdb.dto.*;

import com.khubla.hsclient.domain.*;

public class GenericPointGeneratorImpl implements PointGenerator<Device> {
	@Override
	public Point generatePoint(Device device) {
		try {
			final Double value = device.getValue();
			final Point point = Point.measurement("device").tag("ref", device.getRef().toString()).tag("location1", device.getLocation()).tag("location1", device.getLocation())
					.tag("location2", device.getLocation2()).tag("name", getDeviceName(device)).tag("type", device.getDevice_type_string()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("value", value).addField("status", device.getStatus()).addField("lastchange", device.getLast_change().getTime()).build();
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
