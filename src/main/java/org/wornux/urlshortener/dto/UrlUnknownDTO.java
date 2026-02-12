package org.wornux.urlshortener.dto;

/**
 * Data Transfer Object (DTO) representing a shortened URL. This class encapsulates the original URL
 * and the user who created it. It includes validation to ensure the original URL is valid and the
 * user is not null.
 *
 * @param originalUrl The original URL to be shortened. Must be a valid URL format.
 * @param createdBy The user who created the shortened URL. Cannot be null.
 */
public record UrlUnknownDTO(String originalUrl, String sessionId) {
  /**
   * Compact constructor for UrlDTO. Validates the original URL and the user.
   *
   * @throws IllegalArgumentException if the original URL is invalid or the user is null.
   */
  public UrlUnknownDTO {
    if (originalUrl == null
        || originalUrl.isBlank()
        || !originalUrl.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
      throw new IllegalArgumentException(
          "Original URL cannot be blank and must be a valid URL format");
    }
    if (sessionId == null || sessionId.isBlank()) {
      throw new IllegalArgumentException("Session ID cannot be null or blank");
    }
  }
}
