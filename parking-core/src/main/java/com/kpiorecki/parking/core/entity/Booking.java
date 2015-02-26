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

	private Long id;
	// private Parking parking;
	// private User user;
	private DateTime date;
	private DateTime creationTime;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_bookings")
	@TableGenerator(name = "seq_bookings", pkColumnValue = "seq_bookings")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// @Column(nullable = false)
	// public Parking getParking() {
	// return parking;
	// }
	//
	// public void setParking(Parking parking) {
	// this.parking = parking;
	// }

	// @Column(nullable = false)
	// public User getUser() {
	// return user;
	// }
	//
	// public void setUser(User user) {
	// this.user = user;
	// }

	@Column(nullable = false)
	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	@Column(nullable = false)
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
