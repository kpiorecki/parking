package com.kpiorecki.parking.ejb.entity;

import com.kpiorecki.parking.ejb.entity.Booking.Status;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.LocalDate;

@Generated(value="Dali", date="2015-04-10T21:51:36.731+0200")
@StaticMetamodel(Booking.class)
public class Booking_ {
	public static volatile SingularAttribute<Booking, Long> id;
	public static volatile SingularAttribute<Booking, Parking> parking;
	public static volatile SetAttribute<Booking, BookingEntry> entries;
	public static volatile SingularAttribute<Booking, LocalDate> date;
	public static volatile SingularAttribute<Booking, Status> status;
	public static volatile SingularAttribute<Booking, Integer> version;
}
