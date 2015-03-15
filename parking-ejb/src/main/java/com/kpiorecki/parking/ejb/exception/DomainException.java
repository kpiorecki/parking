package com.kpiorecki.parking.ejb.exception;

public class DomainException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DomainException() {
	}

	public DomainException(String message) {
		super(message);
	}

	public DomainException(Throwable cause) {
		super(cause);
	}

	public DomainException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
