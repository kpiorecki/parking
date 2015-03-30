package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	@JoinColumn(name = "parking_uuid")
	private Parking parking;

	@OneToMany
	@JoinTable(name = "schedule_users", joinColumns = @JoinColumn(name = "schedule_id"), inverseJoinColumns = @JoinColumn(name = "login"))
	private Set<User> users = new HashSet<>();

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

	public Set<User> getUsers() {
		return Collections.unmodifiableSet(users);
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void removeAllUsers() {
		users.clear();
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
