package com.khubla.hsinflux.pointgenerator;

import java.util.concurrent.*;

import org.influxdb.dto.*;

import com.khubla.hsinflux.*;

public class PollPointGeneratorImpl implements PointGenerator<Poll> {
	private static final String MEASUREMENT_NAME = "poll";

	@Override
	public Point generatePoint(Poll poll) {
		try {
			final Point point = Point.measurement(MEASUREMENT_NAME).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("timems", poll.getTimems()).addField("points", poll.getPoints())
					.addField("threads", poll.getThreads()).build();
			return point;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
