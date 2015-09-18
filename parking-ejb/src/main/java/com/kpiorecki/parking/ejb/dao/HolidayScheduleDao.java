package com.kpiorecki.parking.ejb.dao;

import javax.ejb.Stateless;

import com.kpiorecki.parking.ejb.entity.HolidaySchedule;

@Stateless
public class HolidayScheduleDao extends GenericDao<String, HolidaySchedule> {

	public HolidayScheduleDao() {
		super(HolidaySchedule.class);
	}
}
