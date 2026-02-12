package org.wornux.urlshortener.dto;

import java.util.Date;
import org.wornux.urlshortener.model.AccessLog;

/**
 * Data Transfer Object for AccessLog.
 *
 * @param url The URL that was accessed.
 * @param accessedAt The timestamp of when the URL was accessed.
 * @param ipAddress The IP address of the client.
 * @param userAgent The user agent of the client.
 */
public record AccessLogDTO(
    String shortenedUrl, Date accessedAt, String browser, String ipAddress, String os) {
  /**
   * Compact constructor to ensure no fields are null.
   *
   * @param url The URL that was accessed.
   * @param accessedAt The timestamp of when the URL was accessed.
   * @param ipAddress The IP address of the client.
   * @param userAgent The user agent of the client.
   * @throws IllegalArgumentException if any field is null.
   */
  public AccessLogDTO {
    if (shortenedUrl == null
        || accessedAt == null
        || ipAddress == null
        || browser == null
        || os == null
        || shortenedUrl.isBlank()
        || ipAddress.isBlank()
        || browser.isBlank()
        || os.isBlank()) {
      throw new IllegalArgumentException("None of the fields can be null");
    }
  }

  /**
   * Constructs an AccessLogDTO from an AccessLog entity.
   *
   * @param accessLog The AccessLog entity.
   */
  public AccessLogDTO(AccessLog accessLog) {
    this(
        accessLog.getUrl().getOriginalUrl(),
        accessLog.getAccessedAt(),
        accessLog.getBrowser(),
        accessLog.getIpAddress(),
        accessLog.getOs());
  }
}
