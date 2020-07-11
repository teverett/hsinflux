package com.khubla.hsinflux;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.influxdb.*;
import org.influxdb.dto.*;
import org.slf4j.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.poll.*;
import com.khubla.hsinflux.pointgenerator.*;

/**
 * @author Tom Everett
 *         <p>
 *         Copyright (C) 2020,tom@khubla.com
 *         </p>
 */
public class Importer implements DataPointCallback {
	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(Importer.class);
	/**
	 * configuration for hsinflux
	 */
	private final Configuration configuration;
	/**
	 * poller
	 */
	private final Poller poller;
	/**
	 * points queue
	 */
	private Queue<Point> points;

	public Importer(Configuration configuration) {
		super();
		this.configuration = configuration;
		poller = new Poller(configuration.getHsConfiguration(), this.configuration.getPollinginterval(), this, this.configuration.getPollingthreads());
	}

	@Override
	public void beginUpdate() {
		points = new ConcurrentLinkedQueue<Point>();
	}

	@Override
	public void endUpdate(long timems) {
		/*
		 * write points
		 */
		writeDevicePointsToInflux(points);
		/*
		 * write poll data
		 */
		writePollDataToInflux(new Poll(points.size(), configuration.getPollingthreads(), timems));
	}

	public void run() throws HSClientException, InterruptedException, IOException {
		poller.run();
	}

	@Override
	public void update(DataPoint dataPoint) {
		final PointGenerator<DataPoint> pointGenerator = new DevicePointGeneratorImpl();
		final Point point = pointGenerator.generatePoint(dataPoint);
		points.add(point);
	}

	/**
	 * write to device data to influx on a thread
	 *
	 * @param batchPoints
	 */
	private void writeDevicePointsToInflux(Queue<Point> points) {
		/*
		 * make the batch
		 */
		final BatchPoints batchPoints = BatchPoints.database(configuration.getInfluxdb()).build();
		for (final Point point : points) {
			batchPoints.point(point);
		}
		new Thread(() -> {
			InfluxDB influxDB = null;
			try {
				influxDB = InfluxDBFactory.connect(configuration.getInfluxurl(), configuration.getInfluxuser(), configuration.getInfluxpassword());
				influxDB.write(batchPoints);
			} catch (final Exception e) {
				logger.error("Error writing device data to InfluxDB ", e);
			} finally {
				influxDB.close();
			}
		}).start();
	}

	/**
	 * write poll data to influx on a thread
	 *
	 * @param poll
	 */
	private void writePollDataToInflux(Poll poll) {
		new Thread(() -> {
			InfluxDB influxDB = null;
			try {
				final PointGenerator<Poll> pointGenerator = new PollPointGeneratorImpl();
				final Point point = pointGenerator.generatePoint(poll);
				influxDB = InfluxDBFactory.connect(configuration.getInfluxurl(), configuration.getInfluxuser(), configuration.getInfluxpassword());
				influxDB.setDatabase(configuration.getInfluxdb());
				influxDB.write(point);
			} catch (final Exception e) {
				logger.error("Error writing poll data to InfluxDB ", e);
			} finally {
				influxDB.close();
			}
		}).start();
	}
}
