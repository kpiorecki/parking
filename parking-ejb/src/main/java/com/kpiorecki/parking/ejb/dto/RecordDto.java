package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

public class RecordDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private UserDto user;
	private Integer points;
	private Boolean vip;
	private Integer version;

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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
		builder.append(", version=");
		builder.append(version);
		builder.append("]");

		return builder.toString();
	}

}
