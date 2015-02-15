package com.kpiorecki.parking.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.joda.time.DateTime;

@Entity
public class Booking {

	private Integer id;
//	private Parking parking;
//	private User user;
	private DateTime date;
	private DateTime creationTime;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

//	@Column(nullable = false)
//	public Parking getParking() {
//		return parking;
//	}
//
//	public void setParking(Parking parking) {
//		this.parking = parking;
//	}

//	@Column(nullable = false)
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}

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
	private void prePersist() {
		this.creationTime = new DateTime();
	}

}
