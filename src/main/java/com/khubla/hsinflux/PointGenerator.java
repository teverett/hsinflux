package com.khubla.hsinflux;

import org.influxdb.dto.*;

/**
 * @author Tom Everett
 *         <p>
 *         Copyright (C) 2020,tom@khubla.com
 *         </p>
 */
public interface PointGenerator<T> {
	Point generatePoint(T t);
}
