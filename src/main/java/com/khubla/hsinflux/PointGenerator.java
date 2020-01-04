package com.khubla.hsinflux;

import org.influxdb.dto.*;

public interface PointGenerator<T> {
	Point generatePoint(T t);
}
