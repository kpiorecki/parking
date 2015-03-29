package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.joda.time.LocalDate;

@Entity
@Table(name = "schedules")
public class Schedule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_schedules")
	@TableGenerator(name = "seq_schedules", pkColumnValue = "seq_schedules")
	private Long id;

	@Column(nullable = false)
	private LocalDate date;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "parking_fk")
	private Parking parking;

	// private Set<User> users;

	@Version
	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void LocalDate(LocalDate date) {
		this.date = date;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}

	// public Set<User> getUsers() {
	// return users;
	// }

	// public void setUsers(Set<User> users) {
	// this.users = users;
	// }

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
