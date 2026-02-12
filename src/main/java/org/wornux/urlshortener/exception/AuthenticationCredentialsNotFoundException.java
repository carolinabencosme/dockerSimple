package org.wornux.urlshortener.exception;

public class AuthenticationCredentialsNotFoundException extends RuntimeException {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public AuthenticationCredentialsNotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public AuthenticationCredentialsNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
