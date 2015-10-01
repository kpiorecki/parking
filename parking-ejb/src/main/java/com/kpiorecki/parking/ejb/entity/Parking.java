package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Version;

import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@Entity
@Cacheable
@Table(name = "parkings")
@NamedQueries({ @NamedQuery(name = "Parking.findByUser", query = "select r.parking from Record r where r.user.login = :login") })
public class Parking implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = UuidGenerator.UUID_LENGTH)
	private String uuid;

	@Column(nullable = false, length = ParkingDto.NAME_MAX_LEN)
	private String name;

	@Column(nullable = false)
	private Integer capacity;

	@Embedded
	private Address address;

	@OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Record> records = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "holiday_schedule_uuid")
	private HolidaySchedule holidaySchedule;

	@Version
	private Integer version;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Set<Record> getRecords() {
		return new HashSet<Record>(records);
	}

	public void addRecord(Record record) {
		record.setParking(this);
		records.add(record);
	}

	public void removeRecord(Record record) {
		boolean removed = records.remove(record);
		if (removed) {
			record.setParking(null);
		}
	}

	public void removeAllRecords() {
		records.clear();
	}

	public HolidaySchedule getHolidaySchedule() {
		return holidaySchedule;
	}

	public void setHolidaySchedule(HolidaySchedule holidaySchedule) {
		this.holidaySchedule = holidaySchedule;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@PreRemove
	public void clearHolidaySchedule() {
		if (holidaySchedule != null) {
			holidaySchedule.removeParking(this);
		}
	}

}
