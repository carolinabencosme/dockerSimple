package org.wornux.urlshortener.enums;

public enum SecurityConstants {

  /** The secret key used for signing JWT tokens. */
  JWT_SECRET("A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6"),

  /** The expiration time for JWT tokens in milliseconds. */
  JWT_EXPIRATION_TIME(3_600_000L); // 1 hour

  private final Object value;

  SecurityConstants(Object value) {
    this.value = value;
  }

  /**
   * Retrieves the value of the constant.
   *
   * @return the value of the constant
   */
  public Object getValue() {
    return value;
  }
}
