package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.joda.time.LocalDate;

@Entity
@Table(name = "bookings", indexes = { @Index(columnList = "parking_uuid, date", unique = true) })
@NamedQueries({
		@NamedQuery(name = "Booking.findByParkingAndDate", query = "select b from Booking b where b.parking.uuid = :parkingUuid and b.date = :date"),
		@NamedQuery(name = "Booking.findIdsByParking", query = "select b.id from Booking b where b.parking.uuid = :parkingUuid") })
public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status {
		DRAFT,
		RELEASED,
		LOCKED;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_bookings")
	@TableGenerator(name = "seq_bookings", pkColumnValue = "seq_bookings")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "parking_uuid")
	private Parking parking;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "booking_id")
	private Set<BookingEntry> entries = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinTable(name = "booking_users", joinColumns = @JoinColumn(name = "booking_id"), inverseJoinColumns = @JoinColumn(name = "login"), indexes = { @Index(columnList = "booking_id, login", unique = true) })
	private Set<User> users = new HashSet<>();

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.DRAFT;

	@Version
	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}

	public Set<BookingEntry> getEntries() {
		return Collections.unmodifiableSet(entries);
	}

	public void addEntry(BookingEntry entry) {
		entries.add(entry);
	}

	public void removeEntry(BookingEntry entry) {
		entries.remove(entry);
	}

	public Set<User> getUsers() {
		return Collections.unmodifiableSet(users);
	}

	public void setUsers(Set<User> users) {
		this.users.clear();
		this.users.addAll(users);
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
