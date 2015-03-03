package com.kpiorecki.parking.core.service.impl;

import java.util.UUID;

import javax.ejb.Stateless;

@Stateless
public class UuidGenerator {

	public String generateUuid() {
		return UUID.randomUUID().toString();
	}
}
