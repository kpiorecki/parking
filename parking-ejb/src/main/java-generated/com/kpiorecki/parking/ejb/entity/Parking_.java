package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-17T08:18:43.042+0200")
@StaticMetamodel(Parking.class)
public class Parking_ {
	public static volatile SingularAttribute<Parking, String> uuid;
	public static volatile SingularAttribute<Parking, String> name;
	public static volatile SingularAttribute<Parking, Integer> capacity;
	public static volatile SingularAttribute<Parking, Address> address;
	public static volatile SetAttribute<Parking, Record> records;
	public static volatile SingularAttribute<Parking, HolidaySchedule> holidaySchedule;
	public static volatile SingularAttribute<Parking, Integer> version;
}
