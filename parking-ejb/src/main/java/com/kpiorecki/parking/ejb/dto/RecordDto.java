package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

public class RecordDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private UserDto user;
	private Integer points;
	private Boolean vip;

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Boolean getVip() {
		return vip;
	}

	public void setVip(Boolean vip) {
		this.vip = vip;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RecordDto [user=");
		builder.append(user);
		builder.append(", points=");
		builder.append(points);
		builder.append(", vip=");
		builder.append(vip);
		builder.append("]");

		return builder.toString();
	}

}
