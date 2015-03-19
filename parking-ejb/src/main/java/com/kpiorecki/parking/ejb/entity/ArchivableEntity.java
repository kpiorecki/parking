package com.kpiorecki.parking.ejb.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ArchivableEntity {

	@Column(nullable = false)
	private Boolean removed = false;

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}
}
