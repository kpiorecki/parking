package com.kpiorecki.parking.core.util;

import java.util.UUID;

import javax.ejb.Stateless;

@Stateless
public class UuidGenerator {

	public String generateUuid() {
		return UUID.randomUUID().toString();
	}
}
