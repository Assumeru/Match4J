package com.ee.match.exception;

public class ParamPageException extends RuntimeException {
	private static final long serialVersionUID = -3568126798284266883L;

	public ParamPageException() {
		super();
	}

	public ParamPageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParamPageException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParamPageException(String message) {
		super(message);
	}

	public ParamPageException(Throwable cause) {
		super(cause);
	}
}
