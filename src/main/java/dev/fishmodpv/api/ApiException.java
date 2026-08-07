package dev.fishmodpv.api;

/** Thrown for any network/API-level failure so GUI code can show a friendly message. */
public class ApiException extends RuntimeException {
	public ApiException(String message) {
		super(message);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}
}
