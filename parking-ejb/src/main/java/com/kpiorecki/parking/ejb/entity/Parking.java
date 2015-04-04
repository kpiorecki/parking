package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

@Entity
@Cacheable
@Table(name = "parkings")
public class Parking implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String uuid;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer capacity;

	@Embedded
	private Address address;

	@OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Record> records = new HashSet<>();

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
		return Collections.unmodifiableSet(records);
	}

	public void addRecord(Record record) {
		int recordPoints = 0;
		if (!records.isEmpty()) {
			/**
			 * set new record's points to the mean value of existing records points (rounded upwards)
			 */
			Mean mean = new Mean();
			for (Record existingRecord : records) {
				mean.increment(existingRecord.getPoints());
			}
			double pointsMean = mean.getResult();
			recordPoints = (int) new Ceil().value(pointsMean);
		}

		record.setPoints(recordPoints);
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
