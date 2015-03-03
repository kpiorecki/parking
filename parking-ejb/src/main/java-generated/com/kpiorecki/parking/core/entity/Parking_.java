package com.kpiorecki.parking.core.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-03T23:12:07.922+0100")
@StaticMetamodel(Parking.class)
public class Parking_ {
	public static volatile SingularAttribute<Parking, Long> id;
	public static volatile SingularAttribute<Parking, Integer> capacity;
	public static volatile SingularAttribute<Parking, Address> address;
	public static volatile SetAttribute<Parking, Record> records;
	public static volatile SingularAttribute<Parking, Integer> version;
	public static volatile SingularAttribute<Parking, String> uuid;
}
