package com.khubla.hsinflux;

import org.influxdb.dto.*;

/**
 * @author Tom Everett.
 * @copyright Copyright (C) 2020,tom@khubla.com
 */
public interface PointGenerator<T> {
	Point generatePoint(T t);
}
