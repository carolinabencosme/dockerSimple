package org.wornux.urlshortener.dto;

public record Authentication(String username, String password) {
  /**
   * Validates that the username and password are not null or empty.
   *
   * @throws IllegalArgumentException if either field is invalid
   */
  public Authentication {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
  }
}
