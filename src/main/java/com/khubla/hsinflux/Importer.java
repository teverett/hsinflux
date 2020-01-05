package com.khubla.hsinflux;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.influxdb.*;
import org.influxdb.dto.*;

import com.khubla.hsclient.*;
import com.khubla.hsclient.domain.*;

public class Importer {
	private static final String DB_NAME = "house";
	private static final int THREAD_COUNT = 5;
	private static final int CYCLE_TIME_MS = 1000 * 60;
	private final String hsURL;
	private final String hsPassword;
	private final String hsUsername;
	private final String influxURL;
	private final String influxPassword;
	private final String influxUsername;

	public Importer(String hsURL, String hsUsername, String hsPassword, String influxURL, String influxUsername, String influxPassword) {
		super();
		this.hsURL = hsURL;
		this.hsPassword = hsPassword;
		this.hsUsername = hsUsername;
		this.influxURL = influxURL;
		this.influxUsername = influxUsername;
		this.influxPassword = influxPassword;
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

	public String getInfluxPassword() {
		return influxPassword;
	}

	public String getInfluxURL() {
		return influxURL;
	}

	public String getInfluxUsername() {
		return influxUsername;
	}

	public void run() throws HSClientException, InterruptedException, IOException {
		Map<Integer, Device> devices = null;
		HSClient hsClient = null;
		try {
			/*
			 * get devices
			 */
			hsClient = new HSClientImpl(hsURL, hsUsername, hsPassword);
			devices = hsClient.getDevicesByRef();
		} catch (final Exception e) {
			e.printStackTrace();
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
					final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
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
									final PointGenerator<Device> pointGenerator = new GenericPointGeneratorImpl();
									final Point point = pointGenerator.generatePoint(device);
									points.add(point);
								} catch (final Exception e) {
									e.printStackTrace();
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
					writeToInflux(points);
					/*
					 * log the time
					 */
					final long t = System.currentTimeMillis() - start;
					System.out.println("Data collection performed in " + Long.toString(t) + " ms on " + THREAD_COUNT + " threads");
					/*
					 * nap time
					 */
					Thread.sleep(CYCLE_TIME_MS - t);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Device updateDevice(Integer ref) throws HSClientException, IOException {
		HSClient hsClient = null;
		try {
			hsClient = new HSClientImpl(hsURL, hsUsername, hsPassword);
			return hsClient.getDevice(ref);
		} finally {
			hsClient.close();
		}
	}

	/**
	 * write to influx on a thread
	 *
	 * @param batchPoints
	 */
	private void writeToInflux(Queue<Point> points) {
		/*
		 * make the batch
		 */
		final BatchPoints batchPoints = BatchPoints.database(DB_NAME).build();
		for (final Point point : points) {
			batchPoints.point(point);
		}
		new Thread(() -> {
			InfluxDB influxDB = null;
			try {
				influxDB = InfluxDBFactory.connect(influxURL, influxUsername, influxPassword);
				influxDB.write(batchPoints);
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				influxDB.close();
			}
		}).start();
	}
}
