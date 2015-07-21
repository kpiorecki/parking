package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

public class BookingEntryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String login;
	private DateTime creationTime;
	private Boolean accepted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(DateTime creationTime) {
		this.creationTime = creationTime;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

}
