package com.kpiorecki.parking.ejb.service;

import java.util.List;

import javax.ejb.Local;

import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.dto.RecordDto;

@Local
public interface ParkingService {

	String addParking(ParkingDto parkingDto);

	void modifyParking(ParkingDto parkingDto);

	void deleteParking(String parkingUuid);

	void assignUser(String parkingUuid, String login, boolean vip);

	void revokeUser(String parkingUuid, String login);

	void revokeAllUsers(String parkingUuid);
	
	boolean isUserAssigned(String parkingUuid, String login);

	ParkingDto findParking(String parkingUuid);
	
	List<ParkingDto> findAllParkings();

	List<RecordDto> findRecords(String parkingUuid);
}
