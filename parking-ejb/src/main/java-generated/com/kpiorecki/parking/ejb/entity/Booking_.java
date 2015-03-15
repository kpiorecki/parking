package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value="Dali", date="2015-03-14T12:17:10.155+0100")
@StaticMetamodel(Booking.class)
public class Booking_ {
	public static volatile SingularAttribute<Booking, Long> id;
	public static volatile SingularAttribute<Booking, Parking> parking;
	public static volatile SetAttribute<Booking, BookingEntry> entries;
	public static volatile SingularAttribute<Booking, DateTime> date;
	public static volatile SingularAttribute<Booking, Boolean> locked;
	public static volatile SingularAttribute<Booking, Integer> version;
}
