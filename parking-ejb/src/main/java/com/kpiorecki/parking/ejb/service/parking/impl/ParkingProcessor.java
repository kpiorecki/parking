package com.kpiorecki.parking.ejb.service.parking.impl;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Stateless;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.math.DoubleMath;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;

@Stateless
public class ParkingProcessor {

	public int calculateNewRecordPoints(Parking parking) {
		int recordPoints = 0;
		Set<Record> records = parking.getRecords();
		if (!records.isEmpty()) {
			/**
			 * calculate new record's points as mean value of existing records points (rounded upwards)
			 */
			Function<Record, Integer> getPointsFunction = record -> (record != null) ? record.getPoints() : null;
			Iterator<Integer> pointsIterator = Iterators.transform(records.iterator(), getPointsFunction);
			double pointsMean = DoubleMath.mean(pointsIterator);
			recordPoints = DoubleMath.roundToInt(pointsMean, RoundingMode.CEILING);
		}
		return recordPoints;
	}

}
