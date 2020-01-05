package com.khubla.hsinflux;

/**
 * @author Tom Everett.
 * @copyright Copyright (C) 2020,tom@khubla.com
 */
public class Poll {
	private final int points;
	private final int threads;
	private final long timems;

	public Poll(int points, int threads, long timems) {
		super();
		this.points = points;
		this.threads = threads;
		this.timems = timems;
	}

	public int getPoints() {
		return points;
	}

	public int getThreads() {
		return threads;
	}

	public long getTimems() {
		return timems;
	}
}
