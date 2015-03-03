package com.kpiorecki.parking.core.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-02T16:15:23.461+0100")
@StaticMetamodel(Record.class)
public class Record_ {
	public static volatile SingularAttribute<Record, Long> id;
	public static volatile SingularAttribute<Record, Integer> points;
	public static volatile SingularAttribute<Record, Integer> version;
	public static volatile SingularAttribute<Record, Boolean> vip;
}
