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

@Entity
@Table(name = "records")
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_records")
	@TableGenerator(name = "seq_records", pkColumnValue = "seq_records")
	private Long id;

	// @Column(nullable = false)
	// private Parking parking;

	// @Column(nullable = false)
	// private User user;

	@Column(nullable = false)
	private Integer points;

	@Column(nullable = false)
	private Boolean vip;

	@Version
	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// public Parking getParking() {
	// return parking;
	// }

	// public void setParking(Parking parking) {
	// this.parking = parking;
	// }

	// public User getUser() {
	// return user;
	// }

	// public void setUser(User user) {
	// this.user = user;
	// }

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Boolean isVip() {
		return vip;
	}

	public void setVip(Boolean vip) {
		this.vip = vip;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
