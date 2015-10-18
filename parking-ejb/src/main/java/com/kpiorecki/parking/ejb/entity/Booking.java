package com.kpiorecki.parking.ejb.entity;

import static com.kpiorecki.parking.ejb.entity.BookingStatus.LOCKED;
import static com.kpiorecki.parking.ejb.entity.BookingStatus.RELEASED;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.jpa.BookingUpdateStatusListener;

@Entity
@Table(name = "bookings", indexes = { @Index(columnList = "parking_uuid, date", unique = true) })
@EntityListeners(BookingUpdateStatusListener.class)
@NamedQueries({
		@NamedQuery(name = "Booking.findByParkingAndDate", query = "select b from Booking b where b.parking.uuid = :parkingUuid and b.date = :date"),
		@NamedQuery(name = "Booking.findIdsByParking", query = "select b.id from Booking b where b.parking.uuid = :parkingUuid"),
		@NamedQuery(name = "Booking.findByDateRangeAndParkings", query = "select b from Booking b where b.date >= :startDate and b.date < :endDate and b.parking in :parkingList order by b.date") })
public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_bookings")
	@TableGenerator(name = "seq_bookings", pkColumnValue = "seq_bookings")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "parking_uuid")
	private Parking parking;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "booking_id")
	private Set<BookingEntry> entries = new HashSet<>();

	@Column(nullable = false)
	private LocalDate date;

	@Column
	@Enumerated(EnumType.STRING)
	private BookingStatus manualStatus;

	@Transient
	private BookingStatus status;

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
		return new HashSet<BookingEntry>(entries);
	}

	public void addEntry(BookingEntry entry) {
		BookingStatus.validateLowerStatus(status, LOCKED);
		entries.add(entry);
	}

	public void removeEntry(BookingEntry entry) {
		BookingStatus.validateLowerStatus(status, LOCKED);
		entries.remove(entry);
	}

	public void acceptEntries(Set<BookingEntry> acceptedEntries) {
		for (BookingEntry entry : entries) {
			boolean accepted = acceptedEntries.contains(entry);
			entry.setAccepted(accepted);
		}
	}

	public Set<BookingEntry> getAcceptedEntries() {
		Set<BookingEntry> acceptedEntries = new HashSet<>();
		for (BookingEntry entry : entries) {
			if (entry.getAccepted()) {
				acceptedEntries.add(entry);
			}
		}

		return acceptedEntries;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void release() {
		BookingStatus.validateLowerStatus(status, RELEASED);
		setManualStatus(RELEASED);
	}

	public void lock() {
		BookingStatus.validateLowerStatus(status, LOCKED);
		setManualStatus(LOCKED);
	}

	public void updateStatus(BookingStatus defaultStatus) {
		BookingStatus status = defaultStatus;
		if (manualStatus != null) {
			status = BookingStatus.getHigherStatus(manualStatus, defaultStatus);
		}
		this.status = status;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	private void setManualStatus(BookingStatus status) {
		this.manualStatus = status;
		this.status = status;
	}

}
