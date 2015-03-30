package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.LocalDate;

@Generated(value="Dali", date="2015-03-30T12:12:20.509+0200")
@StaticMetamodel(Schedule.class)
public class Schedule_ {
	public static volatile SingularAttribute<Schedule, Long> id;
	public static volatile SingularAttribute<Schedule, LocalDate> date;
	public static volatile SingularAttribute<Schedule, Parking> parking;
	public static volatile SetAttribute<Schedule, User> users;
	public static volatile SingularAttribute<Schedule, Integer> version;
}
