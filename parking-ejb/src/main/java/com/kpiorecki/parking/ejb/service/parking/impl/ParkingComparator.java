package com.kpiorecki.parking.ejb.service.parking.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.kpiorecki.parking.ejb.entity.Parking;

public class ParkingComparator implements Comparator<Parking>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Parking o1, Parking o2) {
		return o1.getName().compareToIgnoreCase(o2.getName());
	}

}
