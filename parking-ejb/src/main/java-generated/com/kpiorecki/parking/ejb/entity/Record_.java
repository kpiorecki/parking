package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-29T20:34:52.748+0200")
@StaticMetamodel(Record.class)
public class Record_ {
	public static volatile SingularAttribute<Record, Long> id;
	public static volatile SingularAttribute<Record, User> user;
	public static volatile SingularAttribute<Record, Parking> parking;
	public static volatile SingularAttribute<Record, Integer> points;
	public static volatile SingularAttribute<Record, Boolean> vip;
	public static volatile SingularAttribute<Record, Integer> version;
}
