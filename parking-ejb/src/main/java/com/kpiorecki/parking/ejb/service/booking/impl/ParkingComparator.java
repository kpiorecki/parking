package com.kpiorecki.parking.ejb.service.booking.impl;

import java.util.Comparator;

import com.kpiorecki.parking.ejb.entity.Parking;

public class ParkingComparator implements Comparator<Parking> {

	@Override
	public int compare(Parking o1, Parking o2) {
		return o1.getName().compareToIgnoreCase(o2.getName());
	}

}
