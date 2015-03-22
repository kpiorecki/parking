package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.joda.time.LocalDate;

@Entity
@Table(name = "bookings", indexes = { @Index(columnList = "parking_fk, date", unique = true) })
@NamedQuery(name = "Booking.findByParkingAndDate", query = "select b from Booking b where b.parking.uuid = :parkingUuid and b.date = :date")
public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_bookings")
	@TableGenerator(name = "seq_bookings", pkColumnValue = "seq_bookings")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "parking_fk")
	private Parking parking;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "booking_fk")
	private Set<BookingEntry> entries = new HashSet<>();

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private Boolean locked = false;

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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
