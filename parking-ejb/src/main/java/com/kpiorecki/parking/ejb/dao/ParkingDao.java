package com.kpiorecki.parking.ejb.dao;

import javax.ejb.Stateless;

import com.kpiorecki.parking.ejb.entity.Parking;

@Stateless
public class ParkingDao extends GenericDao<String, Parking> {

	public ParkingDao() {
		super(Parking.class);
	}

}
