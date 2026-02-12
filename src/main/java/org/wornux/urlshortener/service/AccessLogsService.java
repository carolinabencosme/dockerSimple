package org.wornux.urlshortener.service;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.dao.AccessLogsDAO;
import org.wornux.urlshortener.dto.AccessLogDTO;
import org.wornux.urlshortener.model.AccessLog;
import org.wornux.urlshortener.model.Url;

/** Service for managing access logs. */
public class AccessLogsService {
  private final AccessLogsDAO accessLogsDAO;
  private final UrlService urlService;

  /**
   * Constructor to initialize the AccessLogsService with the required DAO.
   *
   * @param accessLogsDAO DAO for managing access logs.
   */
  public AccessLogsService(AccessLogsDAO accessLogsDAO, UrlService urlService) {
    this.accessLogsDAO = accessLogsDAO;
    this.urlService = urlService;
  }

  /**
   * Retrieves all access logs.
   *
   * @return A list of AccessLogDTO objects.
   */
  public List<AccessLogDTO> getAllAccessLogs() {
    return accessLogsDAO.findAll().stream().map(AccessLogDTO::new).toList();
  }

  /**
   * Retrieves an access log by its ID.
   *
   * @param id The ObjectId of the access log.
   * @return An Optional containing the AccessLogDTO if found.
   */
  public Optional<AccessLogDTO> getAccessLogById(ObjectId id) {
    Optional<AccessLog> accessLog = accessLogsDAO.findById(id);
    if (accessLog.isEmpty()) {
      throw new IllegalArgumentException("Access log not found");
    }
    return Optional.of(new AccessLogDTO(accessLog.get()));
  }

  /**
   * Creates a new access log.
   *
   * @param accessLogDTO The DTO containing access log details.
   */
  public void createAccessLog(AccessLogDTO accessLogDTO) {
    if (accessLogDTO == null) {
      throw new IllegalArgumentException("Access log cannot be null");
    }
    Url url =
        urlService
            .getShortenedUrlByHash(accessLogDTO.shortenedUrl())
            .orElseThrow(() -> new IllegalArgumentException("Shortened URL not found"));

    urlService.incrementUrlAccessCount(url.getId());
    AccessLog accessLog =
        new AccessLog(url, accessLogDTO.browser(), accessLogDTO.ipAddress(), accessLogDTO.os());
    accessLogsDAO.save(accessLog);
  }

  /**
   * Deletes an access log by its ID.
   *
   * @param id The ObjectId of the access log to delete.
   */
  public void deleteAccessLog(ObjectId id) {
    accessLogsDAO.deleteById(id);
  }
}
