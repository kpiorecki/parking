package com.kpiorecki.parking.core.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-08T15:02:52.713+0100")
@StaticMetamodel(Record.class)
public class Record_ {
	public static volatile SingularAttribute<Record, Long> id;
	public static volatile SingularAttribute<Record, User> user;
	public static volatile SingularAttribute<Record, Integer> points;
	public static volatile SingularAttribute<Record, Boolean> vip;
	public static volatile SingularAttribute<Record, Integer> version;
}
