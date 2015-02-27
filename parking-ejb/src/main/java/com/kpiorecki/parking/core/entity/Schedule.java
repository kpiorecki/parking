package com.kpiorecki.parking.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.joda.time.DateTime;

@Entity
@Table(name = "schedules")
public class Schedule implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private DateTime date;
	// private Parking parking;
	private Boolean locked;
	// private Set<User> users;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_schedules")
	@TableGenerator(name = "seq_schedules", pkColumnValue = "seq_schedules")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false)
	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	// @Column(nullable = false)
	// public Parking getParking() {
	// return parking;
	// }
	//
	// public void setParking(Parking parking) {
	// this.parking = parking;
	// }

	@Column(nullable = false)
	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	// public Set<User> getUsers() {
	// return users;
	// }
	//
	// public void setUsers(Set<User> users) {
	// this.users = users;
	// }

	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
