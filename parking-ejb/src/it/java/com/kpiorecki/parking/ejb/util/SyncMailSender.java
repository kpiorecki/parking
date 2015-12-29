package com.kpiorecki.parking.ejb.util;

import javax.ejb.Stateless;
import javax.enterprise.inject.Specializes;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;

/**
 * Specialized MailSender that sends mails synchronously
 */
@Specializes
@Stateless
public class SyncMailSender extends MailSender {

	@Override
	public void sendBookingAssignedMail(User user, Parking parking, LocalDate date) {
		super.sendBookingAssignedMail(user, parking, date);
	}

	@Override
	public void sendBookingRevokedMail(User user, Parking parking, LocalDate date) {
		super.sendBookingRevokedMail(user, parking, date);
	}

	@Override
	public void sendRegisterMail(User user, String activationURL, DateTime activationDeadline) {
		super.sendRegisterMail(user, activationURL, activationDeadline);
	}

	@Override
	public void sendResetPasswordMail(User user, String resetPasswordURL, int validityHours) {
		super.sendResetPasswordMail(user, resetPasswordURL, validityHours);
	}
}
