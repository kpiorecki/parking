package com.kpiorecki.parking.ejb.entity;

import static com.kpiorecki.parking.ejb.entity.BookingStatus.DRAFT;
import static com.kpiorecki.parking.ejb.entity.BookingStatus.LOCKED;
import static com.kpiorecki.parking.ejb.entity.BookingStatus.RELEASED;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BookingStatusTest {

	@Test
	public void shouldFindHigherStatus1() {
		shouldFindHigherStatus(DRAFT, DRAFT, DRAFT);
	}

	@Test
	public void shouldFindHigherStatus2() {
		shouldFindHigherStatus(DRAFT, RELEASED, RELEASED);
	}

	@Test
	public void shouldFindHigherStatus3() {
		shouldFindHigherStatus(RELEASED, DRAFT, RELEASED);
	}

	@Test
	public void shouldFindHigherStatus4() {
		shouldFindHigherStatus(RELEASED, RELEASED, RELEASED);
	}

	@Test
	public void shouldFindHigherStatus5() {
		shouldFindHigherStatus(LOCKED, RELEASED, LOCKED);
	}

	@Test
	public void shouldFindHigherStatus6() {
		shouldFindHigherStatus(LOCKED, DRAFT, LOCKED);
	}

	private void shouldFindHigherStatus(BookingStatus status1, BookingStatus status2, BookingStatus expectedResult) {
		// when
		BookingStatus status = BookingStatus.getHigherStatus(status1, status2);

		// then
		assertEquals(expectedResult, status);
	}

}
