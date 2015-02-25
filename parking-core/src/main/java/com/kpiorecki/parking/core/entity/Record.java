package com.kpiorecki.parking.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

@Entity
@Table(name = "records")
public class Record {

	private Long id;
	// private Parking parking;
	// private User user;
	private Integer points;
	private Boolean vip;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_records")
	@TableGenerator(name = "seq_records", pkColumnValue = "seq_records")
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
	//
	// @Column(nullable = false)
	// public User getUser() {
	// return user;
	// }
	//
	// public void setUser(User user) {
	// this.user = user;
	// }

	@Column(nullable = false)
	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	@Column(nullable = false)
	public Boolean isVip() {
		return vip;
	}

	public void setVip(Boolean vip) {
		this.vip = vip;
	}

	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
