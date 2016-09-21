package com.ee.match.exception;

public class NoSuchListException extends StateException {
	private static final long serialVersionUID = 7811496412021811515L;

	public NoSuchListException() {
		super();
	}

	public NoSuchListException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchListException(String message) {
		super(message);
	}
}
