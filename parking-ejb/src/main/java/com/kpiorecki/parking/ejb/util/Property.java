package com.kpiorecki.parking.ejb.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
public @interface Property {

	@Nonbinding
	String value();

	@Nonbinding
	int minIntValue() default Integer.MIN_VALUE;

	@Nonbinding
	int maxIntValue() default Integer.MAX_VALUE;

	@Nonbinding
	double minDoubleValue() default Double.MIN_VALUE;

	@Nonbinding
	double maxDoubleValue() default Double.MAX_VALUE;
}
