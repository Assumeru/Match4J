package com.ee.match.exception;

public class StateException extends Exception {
	private static final long serialVersionUID = -678461696944699274L;

	public StateException() {
		super();
	}

	public StateException(String message, Throwable cause) {
		super(message, cause);
	}

	public StateException(String message) {
		super(message);
	}
}
