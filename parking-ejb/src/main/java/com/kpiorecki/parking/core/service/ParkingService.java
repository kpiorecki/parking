package com.kpiorecki.parking.core.service;

import javax.ejb.Local;

import com.kpiorecki.parking.core.dto.ParkingDto;

@Local
public interface ParkingService {

	String addParking(ParkingDto parkingDto);

	void modifyParking(ParkingDto parkingDto);

	void deleteParking(String parkingUuid);

	ParkingDto findParking(String parkingUuid);
	
	void addUser(String parkingUuid, String login);

}
