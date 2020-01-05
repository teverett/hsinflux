package com.khubla.hsinflux;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.influxdb.*;
import org.influxdb.dto.*;
import org.slf4j.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;
import com.khubla.hsinflux.pointgenerator.*;

public class Importer {
	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(Importer.class);
	/**
	 * configuration for hsinflux
	 */
	private final Configuration configuration;

	public Importer(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public void run() throws HSClientException, InterruptedException, IOException {
		Map<Integer, Device> devices = null;
		HSClient hsClient = null;
		try {
			/*
			 * get devices
			 */
			hsClient = new HSClientImpl(configuration.getHsurl(), configuration.getHsuser(), configuration.getHspassword());
			devices = hsClient.getDevicesByRef();
		} catch (final Exception e) {
			logger.error("Error getting devices from HomeSeer", e);
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
					final long start = System.currentTimeMillis();
					/*
					 * thread pool
					 */
					final ExecutorService executorService = Executors.newFixedThreadPool(configuration.getPollingthreads());
					/*
					 * list
					 */
					final Queue<Point> points = new ConcurrentLinkedQueue<Point>();
					/*
					 * walk devices
					 */
					for (final Integer ref : devices.keySet()) {
						/*
						 * runnable
						 */
						final Runnable worker = new Runnable() {
							@Override
							public void run() {
								try {
									final Device device = updateDevice(ref);
									if (null != device) {
										final PointGenerator<Device> pointGenerator = new DevicePointGeneratorImpl();
										final Point point = pointGenerator.generatePoint(device);
										points.add(point);
									}
								} catch (final Exception e) {
									logger.error("Error collecting data for device " + ref, e);
								}
							}
						};
						/*
						 * add to service
						 */
						executorService.execute(worker);
					}
					/*
					 * wait
					 */
					executorService.shutdown();
					executorService.awaitTermination(60, TimeUnit.SECONDS);
					/*
					 * write
					 */
					writeDevicePointsToInflux(points);
					/*
					 * log the time
					 */
					final long t = System.currentTimeMillis() - start;
					/*
					 * write poll data
					 */
					writePollDataToInflux(new Poll(points.size(), configuration.getPollingthreads(), t));
					/*
					 * log
					 */
					logger.info("Data collection of " + points.size() + " points performed in " + Long.toString(t) + " ms on " + configuration.getPollingthreads() + " threads");
					/*
					 * nap time
					 */
					Thread.sleep((configuration.getPollinginterval() * 60 * 1000) - t);
				} catch (final Exception e) {
					logger.error("Error polling", e);
				}
			}
		}
	}

	private Device updateDevice(Integer ref) throws HSClientException, IOException {
		HSClient hsClient = null;
		try {
			hsClient = new HSClientImpl(configuration.getHsurl(), configuration.getHsuser(), configuration.getHspassword());
			return hsClient.getDevice(ref);
		} catch (final Exception e) {
			logger.error("Error updating device " + ref, e);
			return null;
		} finally {
			hsClient.close();
		}
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
