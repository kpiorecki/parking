package com.kpiorecki.parking.core.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-02T16:15:23.460+0100")
@StaticMetamodel(Parking.class)
public class Parking_ extends UuidEntity_ {
	public static volatile SingularAttribute<Parking, Long> id;
	public static volatile SingularAttribute<Parking, Integer> capacity;
	public static volatile SingularAttribute<Parking, Address> address;
	public static volatile SingularAttribute<Parking, Integer> version;
	public static volatile SetAttribute<Parking, Record> records;
}
