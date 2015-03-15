package com.kpiorecki.parking.ejb.jpa;

import java.io.Serializable;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.joda.time.DateTime;

import com.kpiorecki.parking.ejb.jpa.DateOnlyConverter;

@Entity
public class DateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Convert(converter = DateOnlyConverter.class)
	private DateTime date;

}
