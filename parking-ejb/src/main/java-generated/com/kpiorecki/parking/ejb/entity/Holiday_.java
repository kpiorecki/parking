package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.LocalDate;

@Generated(value="Dali", date="2015-09-19T08:06:28.001+0200")
@StaticMetamodel(Holiday.class)
public class Holiday_ {
	public static volatile SingularAttribute<Holiday, Long> id;
	public static volatile SingularAttribute<Holiday, LocalDate> date;
	public static volatile SingularAttribute<Holiday, Boolean> repeatedEveryYear;
	public static volatile SingularAttribute<Holiday, String> note;
}
