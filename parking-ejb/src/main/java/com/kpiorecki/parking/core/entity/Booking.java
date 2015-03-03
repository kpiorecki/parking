package com.kpiorecki.parking.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.joda.time.DateTime;

@Entity
@Table(name = "bookings")
public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_bookings")
	@TableGenerator(name = "seq_bookings", pkColumnValue = "seq_bookings")
	private Long id;

	// @Column(nullable = false)
	// private Parking parking;

	// @Column(nullable = false)
	// private User user;

	@Column(nullable = false)
	private DateTime date;

	@Column(nullable = false, updatable = false)
	private DateTime creationTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// public Parking getParking() {
	// return parking;
	// }
	//
	// public void setParking(Parking parking) {
	// this.parking = parking;
	// }

	// public User getUser() {
	// return user;
	// }
	//
	// public void setUser(User user) {
	// this.user = user;
	// }

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(DateTime creationTime) {
		this.creationTime = creationTime;
	}

	@PrePersist
	protected void prePersist() {
		this.creationTime = new DateTime();
	}

}
