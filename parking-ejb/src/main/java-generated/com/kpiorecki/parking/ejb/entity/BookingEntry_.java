package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value="Dali", date="2015-04-10T21:52:25.208+0200")
@StaticMetamodel(BookingEntry.class)
public class BookingEntry_ {
	public static volatile SingularAttribute<BookingEntry, Long> id;
	public static volatile SingularAttribute<BookingEntry, User> user;
	public static volatile SingularAttribute<BookingEntry, DateTime> creationTime;
	public static volatile SingularAttribute<BookingEntry, Boolean> accepted;
}
