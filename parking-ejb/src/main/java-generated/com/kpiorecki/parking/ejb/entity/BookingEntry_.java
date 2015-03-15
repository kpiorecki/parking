package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value="Dali", date="2015-03-14T12:17:10.156+0100")
@StaticMetamodel(BookingEntry.class)
public class BookingEntry_ {
	public static volatile SingularAttribute<BookingEntry, Long> id;
	public static volatile SingularAttribute<BookingEntry, User> user;
	public static volatile SingularAttribute<BookingEntry, DateTime> creationTime;
}
