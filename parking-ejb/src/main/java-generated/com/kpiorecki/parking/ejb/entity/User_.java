package com.kpiorecki.parking.ejb.entity;

import com.kpiorecki.parking.ejb.entity.User.Group;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-23T11:34:54.709+0200")
@StaticMetamodel(User.class)
public class User_ extends ArchivableEntity_ {
	public static volatile SingularAttribute<User, String> login;
	public static volatile SingularAttribute<User, String> firstName;
	public static volatile SingularAttribute<User, String> lastName;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SetAttribute<User, Group> groups;
	public static volatile SingularAttribute<User, Integer> version;
}
