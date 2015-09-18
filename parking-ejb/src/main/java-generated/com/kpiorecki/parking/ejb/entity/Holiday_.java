package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.LocalDate;

@Generated(value="Dali", date="2015-09-18T04:23:25.352+0200")
@StaticMetamodel(Holiday.class)
public class Holiday_ {
	public static volatile SingularAttribute<Holiday, LocalDate> date;
	public static volatile SingularAttribute<Holiday, Boolean> repeatedEveryYear;
	public static volatile SingularAttribute<Holiday, String> note;
	public static volatile SingularAttribute<Holiday, Long> id;
}
