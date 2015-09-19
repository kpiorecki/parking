package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-19T08:06:28.032+0200")
@StaticMetamodel(HolidaySchedule.class)
public class HolidaySchedule_ {
	public static volatile SingularAttribute<HolidaySchedule, String> uuid;
	public static volatile SingularAttribute<HolidaySchedule, String> name;
	public static volatile SetAttribute<HolidaySchedule, Parking> parkings;
	public static volatile SetAttribute<HolidaySchedule, Holiday> holidays;
	public static volatile SingularAttribute<HolidaySchedule, Integer> dayOfWeekMask;
	public static volatile SingularAttribute<HolidaySchedule, Integer> version;
}
