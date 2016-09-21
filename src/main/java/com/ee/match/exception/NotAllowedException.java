package com.ee.match.exception;

public class NotAllowedException extends StateException {
	private static final long serialVersionUID = 423784223241340104L;

	public NotAllowedException() {
		super();
	}

	public NotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAllowedException(String message) {
		super(message);
	}
}
