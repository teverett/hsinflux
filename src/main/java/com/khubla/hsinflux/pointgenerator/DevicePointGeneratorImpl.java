package com.khubla.hsinflux.pointgenerator;

import java.util.concurrent.*;

import org.influxdb.dto.*;

import com.khubla.hsclient.poll.*;
import com.khubla.hsinflux.*;

/**
 * @author Tom Everett
 *         <p>
 *         Copyright (C) 2020,tom@khubla.com
 *         </p>
 */
public class DevicePointGeneratorImpl implements PointGenerator<DataPoint> {
	private static final String MEASUREMENT_NAME = "device";

	@Override
	public Point generatePoint(DataPoint dataPoint) {
		try {
			final Point point = Point.measurement(MEASUREMENT_NAME).tag("ref", dataPoint.getDeviceRef().toString()).tag("location1", dataPoint.getLocation()).tag("location2", dataPoint.getLocation2())
					.tag("name", getDeviceName(dataPoint.getName())).tag("type", dataPoint.getType()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("value", dataPoint.getValue())
					.addField("status", dataPoint.getStatus()).addField("lastchange", dataPoint.getLastChange()).build();
			return point;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getDeviceName(String n) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < n.length(); i++) {
			if ((((n.charAt(i) >= 'a') && (n.charAt(i) <= 'z'))) || (((n.charAt(i) >= 'A') && (n.charAt(i) <= 'Z')))) {
				stringBuilder.append(n.charAt(i));
			}
		}
		return stringBuilder.toString();
	}
}
