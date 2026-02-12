package org.wornux.urlshortener.dto;

import org.wornux.urlshortener.model.User;

/**
 * Data Transfer Object (DTO) representing a shortened URL. This class encapsulates the original URL
 * and the user who created it. It includes validation to ensure the original URL is valid and the
 * user is not null.
 *
 * @param originalUrl The original URL to be shortened. Must be a valid URL format.
 * @param createdBy The user who created the shortened URL. Cannot be null.
 */
public record UrlDTO(String originalUrl, User createdBy) {
  /**
   * Compact constructor for UrlDTO. Validates the original URL and the user.
   *
   * @throws IllegalArgumentException if the original URL is invalid or the user is null.
   */
  public UrlDTO {
    if (originalUrl == null) {
      throw new IllegalArgumentException("Original URL cannot be null");
    } else if (originalUrl.isBlank()) {
      throw new IllegalArgumentException("Original URL cannot be blank");
    } else if (!originalUrl.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
      throw new IllegalArgumentException("Original URL must be a valid URL format");
    }
    if (createdBy == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
  }
}
