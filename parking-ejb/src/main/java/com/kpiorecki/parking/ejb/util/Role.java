package com.kpiorecki.parking.ejb.util;

import com.kpiorecki.parking.ejb.entity.UserGroup;

/**
 * Defines String constants used as declarative authorization roles in the application. The constants values have to
 * match {@link UserGroup} enumeration names.
 */
public interface Role {

	String USER = "USER";
	String ADMIN = "ADMIN";

}
