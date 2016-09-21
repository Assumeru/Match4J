package com.ee.match.exception;

public class NoSuchWordException extends StateException {
	private static final long serialVersionUID = -8420669656976076692L;

	public NoSuchWordException() {
		super();
	}

	public NoSuchWordException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchWordException(String message) {
		super(message);
	}
}
