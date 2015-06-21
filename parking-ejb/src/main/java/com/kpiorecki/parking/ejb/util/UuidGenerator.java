package com.kpiorecki.parking.ejb.util;

import java.util.UUID;

import javax.ejb.Stateless;

@Stateless
public class UuidGenerator {

	public static final int UUID_LENGTH = 36;

	public String generateUuid() {
		return UUID.randomUUID().toString();
	}
}
