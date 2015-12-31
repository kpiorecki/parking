package com.kpiorecki.parking.ejb.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value="Dali", date="2015-12-31T14:56:57.950+0100")
@StaticMetamodel(User.class)
public class User_ extends ArchivableEntity_ {
	public static volatile SingularAttribute<User, String> login;
	public static volatile SingularAttribute<User, String> firstName;
	public static volatile SingularAttribute<User, String> lastName;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SingularAttribute<User, String> activationUuid;
	public static volatile SingularAttribute<User, DateTime> activationDeadline;
	public static volatile SingularAttribute<User, String> resetPasswordUuid;
	public static volatile SingularAttribute<User, DateTime> resetPasswordDeadline;
	public static volatile SetAttribute<User, UserGroup> groups;
	public static volatile SingularAttribute<User, Integer> version;
}
