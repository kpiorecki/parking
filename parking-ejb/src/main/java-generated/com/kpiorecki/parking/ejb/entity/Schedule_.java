package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value="Dali", date="2015-03-14T12:17:10.158+0100")
@StaticMetamodel(Schedule.class)
public class Schedule_ {
	public static volatile SingularAttribute<Schedule, Long> id;
	public static volatile SingularAttribute<Schedule, DateTime> date;
	public static volatile SingularAttribute<Schedule, Integer> version;
}
