package com.kpiorecki.parking.core.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@MappedSuperclass
public class UuidEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;

	@Column(nullable = false, unique = true, updatable = false, length = 36)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@PrePersist
	protected void prePersist() {
		uuid = UUID.randomUUID().toString();
	}

}
