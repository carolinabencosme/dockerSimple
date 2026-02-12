package org.wornux.urlshortener.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.wornux.urlshortener.dao.AccessLogsDAO;
import org.wornux.urlshortener.dao.LinkPreviewDAO;
import org.wornux.urlshortener.dao.UrlDAO;
import org.wornux.urlshortener.dto.AnalyticsDTO;
import org.wornux.urlshortener.dto.UrlCreatedDTO;
import org.wornux.urlshortener.dto.UrlCreatedFullDTO;
import org.wornux.urlshortener.dto.UrlDTO;
import org.wornux.urlshortener.dto.UrlResponseDTO;
import org.wornux.urlshortener.dto.UrlStatsDTO;
import org.wornux.urlshortener.dto.UrlUnknownDTO;
import org.wornux.urlshortener.model.AccessLog;
import org.wornux.urlshortener.model.LinkPreview;
import org.wornux.urlshortener.model.Url;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.util.LinkPreviewGenerator;
import org.wornux.urlshortener.util.QRCodeGenerator;
import org.wornux.urlshortener.util.UrlShortener;

public class UrlService {
  private final UrlDAO urlDAO;
  private final AccessLogsDAO accessLogsDAO;
  private final LinkPreviewDAO linkPreviewDAO;

  public UrlService(
      UrlDAO urlDAO,
      AccessLogsDAO accessLogsDAO,
      AccessLogsDAO accessLogsDAO1,
      LinkPreviewDAO linkPreviewDAO) {
    this.urlDAO = urlDAO;
    this.accessLogsDAO = accessLogsDAO1;
    this.linkPreviewDAO = linkPreviewDAO;
  }

  public List<UrlCreatedDTO> getAllShortenedUrls() {
    return urlDAO.findAll().stream().map(UrlCreatedDTO::new).toList();
  }

  public List<UrlCreatedDTO> getShortenedUrlsByUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    return urlDAO.findByCreatedBy(user).stream()
        .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
        .map(UrlCreatedDTO::new)
        .toList();
  }

  public List<UrlCreatedDTO> getUrlsBySession(@NotNull String sessionId) {
    return urlDAO.findBySession(sessionId).stream()
        .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
        .map(UrlCreatedDTO::new)
        .toList();
  }

  public Optional<UrlCreatedDTO> getById(ObjectId id) {
    return urlDAO.findById(id).map(UrlCreatedDTO::new);
  }

  public void createShortenedUrl(UrlDTO urlDTO) {
    if (urlDTO == null) {
      throw new IllegalArgumentException("Shortened URL cannot be null");
    }
    try {
      String originalUrl = urlDTO.originalUrl();
      String shortUrl = UrlShortener.generateShortUrl();
      byte[] qrCode;
      qrCode = QRCodeGenerator.generateQRCode(originalUrl);
      Url url = new Url(originalUrl, shortUrl, urlDTO.createdBy(), qrCode);
      urlDAO.save(url);
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate QR code", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid URL format", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create shortened URL", e);
    }
  }

  public void createShortenedUrl(UrlUnknownDTO urlDTO) {
    if (urlDTO == null) {
      throw new IllegalArgumentException("Shortened URL cannot be null");
    }
    try {
      String originalUrl = urlDTO.originalUrl();
      String shortUrl = UrlShortener.generateShortUrl();
      byte[] qrCode;
      qrCode = QRCodeGenerator.generateQRCode(originalUrl);
      Url url = new Url(originalUrl, shortUrl, urlDTO.sessionId(), qrCode);
      urlDAO.save(url);
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate QR code", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid URL format", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create shortened URL", e);
    }
  }

  public Optional<UrlCreatedDTO> getShortenedUrlById(ObjectId id) {
    Optional<Url> shortenedUrl = urlDAO.findById(id);
    if (shortenedUrl.isEmpty()) {
      throw new IllegalArgumentException("Shortened URL not found");
    }
    return Optional.ofNullable(new UrlCreatedDTO(shortenedUrl.get()));
  }

  public void deleteShortenedUrl(ObjectId id) {
    urlDAO.deleteById(id);
  }

  public Optional<Url> getShortenedUrlByHash(String shortUrl) {
    Optional<Url> shortenedUrl = urlDAO.findByHash(shortUrl);
    if (shortenedUrl.isEmpty()) {
      throw new IllegalArgumentException("Shortened URL not found");
    }
    return shortenedUrl;
  }

  public void incrementClickCount(ObjectId id) {
    Optional<Url> shortenedUrl = urlDAO.findById(id);
    if (shortenedUrl.isPresent()) {
      Url url = shortenedUrl.get();
      url.setClickCount(url.getClickCount() + 1);
      urlDAO.save(url);
    } else {
      throw new IllegalArgumentException("Shortened URL not found");
    }
  }

  public List<UrlResponseDTO> getUrlsByUser(User user) {
    if (user == null) {}

    List<Url> urls = urlDAO.findByCreatedBy(user);
    List<UrlResponseDTO> urlResponseDTOs = new ArrayList<>();
    for (Url url : urls) {
      List<AccessLog> logs = accessLogsDAO.findByUrl(url);
      List<AnalyticsDTO> analytics =
          logs.stream()
              .map(
                  log ->
                      new AnalyticsDTO(
                          new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(log.getAccessedAt()),
                          log.getBrowser(),
                          log.getIpAddress(),
                          log.getOs()))
              .toList();
      UrlResponseDTO urlResponseDTO =
          new UrlResponseDTO(
              url.getOriginalUrl(),
              url.getShortenedUrl(),
              url.getCreatedBy().getUsername(),
              new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(url.getCreatedAt()),
              analytics);
      urlResponseDTOs.add(urlResponseDTO);
    }

    return urlResponseDTOs;
  }

  public UrlCreatedFullDTO createFullUrlRecord(UrlDTO urlDTO) {
    if (urlDTO == null || urlDTO.originalUrl() == null || urlDTO.createdBy() == null) {
      throw new IllegalArgumentException("Faltan datos para crear la URL");
    }

    try {
      // 1. Generar URL corta
      String shortUrl = UrlShortener.generateShortUrl();

      // 2. Generar código QR
      byte[] qrCode = QRCodeGenerator.generateQRCode(urlDTO.originalUrl());

      // 3. Crear entidad URL
      Url url = new Url(urlDTO.originalUrl(), shortUrl, urlDTO.createdBy(), qrCode);
      urlDAO.save(url);

      // 4. Vista previa en base64
      String previewBase64 = LinkPreviewGenerator.captureBase64Preview(urlDTO.originalUrl());

      // Crear entidad LinkPreview
      LinkPreview linkPreview = new LinkPreview(url, previewBase64);
      linkPreviewDAO.save(linkPreview);

      // 5. Obtener estadísticas iniciales (vacías)
      List<AccessLog> logs = accessLogsDAO.findByUrl(url); // Puede estar vacío
      int totalAccesses = logs.size();
      int uniqueVisitors = (int) logs.stream().map(AccessLog::getIpAddress).distinct().count();
      Date lastAccess =
          logs.stream().map(AccessLog::getAccessedAt).max(Date::compareTo).orElse(null);
      List<String> userAgents = logs.stream().map(AccessLog::getBrowser).toList();
      List<String> operatingSystems = logs.stream().map(AccessLog::getOs).toList();

      UrlStatsDTO stats =
          new UrlStatsDTO(
              url.getId(),
              url.getOriginalUrl(),
              url.getShortenedUrl(),
              url.getCreatedAt(),
              url.getClickCount(),
              totalAccesses,
              uniqueVisitors,
              lastAccess,
              userAgents,
              operatingSystems);

      return new UrlCreatedFullDTO(
          url.getOriginalUrl(), url.getShortenedUrl(), url.getCreatedAt(), stats, previewBase64);

    } catch (Exception e) {
      throw new RuntimeException("Error al crear URL completa", e);
    }
  }

  public void migrateSessionUrlsToUser(@NotNull String sessionId, @NotNull User user) {
    urlDAO.migrateSessionUrlsToUser(sessionId, user);
  }

  public void update(@NotNull Url u) {
    urlDAO.update(u);
  }

  public void deleteById(@NotNull ObjectId id) {
    urlDAO.deleteById(id);
  }

  public void incrementUrlAccessCount(ObjectId id) {
    Optional<Url> shortenedUrl = urlDAO.findById(id);
    if (shortenedUrl.isPresent()) {
      Url url = shortenedUrl.get();
      url.setClickCount(url.getClickCount() + 1);
      urlDAO.save(url);
    } else {
      throw new IllegalArgumentException("Shortened URL not found");
    }
  }

  public Map<String, Integer> getAnaliticsByUser(@NotNull User user) {
    List<Url> urls = urlDAO.findByCreatedBy(user);
    Map<String, Integer> analytics = new HashMap<>();

    for (Url url : urls) {
      List<AccessLog> logs = accessLogsDAO.findByUrl(url);
      for (AccessLog log : logs) {
        Date date = log.getAccessedAt();
        String formattedDate = new SimpleDateFormat("dd-mm-yyy HH a").format(date);
        analytics.put(formattedDate, analytics.getOrDefault(formattedDate, 0) + 1);
      }
    }

    return analytics;
  }

  public Map<String, Integer> getAnaliticsBySession(@NotNull String sessionId) {
    List<Url> urls = urlDAO.findBySession(sessionId);
    Map<String, Integer> analytics = new HashMap<>();

    for (Url url : urls) {
      List<AccessLog> logs = accessLogsDAO.findByUrl(url);
      for (AccessLog log : logs) {
        Date date = log.getAccessedAt();
        String formattedDate = new SimpleDateFormat("dd-mm-yyy hh a").format(date);
        analytics.put(formattedDate, analytics.getOrDefault(formattedDate, 0) + 1);
      }
    }

    return analytics;
  }

  public Map<String, Integer> getAnaliticsBySessionAndHash(String sessionId, String hash) {
    List<Url> urls = urlDAO.findBySession(sessionId);
    Map<String, Integer> analytics = new HashMap<>();

    for (Url url : urls) {
      if (url.getShortenedUrl().equals(hash)) {
        List<AccessLog> logs = accessLogsDAO.findByUrl(url);
        for (AccessLog log : logs) {
          Date date = log.getAccessedAt();
          String formattedDate = new SimpleDateFormat("dd-mm-yyy hh a").format(date);
          analytics.put(formattedDate, analytics.getOrDefault(formattedDate, 0) + 1);
        }
      }
    }

    return analytics;
  }

  public Map<String, Integer> getAnaliticsByUserAndHash(User user, String hash) {
    List<Url> urls = urlDAO.findByCreatedBy(user);
    Map<String, Integer> analytics = new HashMap<>();

    for (Url url : urls) {
      if (url.getShortenedUrl().equals(hash)) {
        List<AccessLog> logs = accessLogsDAO.findByUrl(url);
        for (AccessLog log : logs) {
          Date date = log.getAccessedAt();
          String formattedDate = new SimpleDateFormat("dd-mm-yyy hh a").format(date);
          analytics.put(formattedDate, analytics.getOrDefault(formattedDate, 0) + 1);
        }
      }
    }

    return analytics;
  }

  public List<UrlCreatedFullDTO> getFullUrlsByUser(User user) {
    return urlDAO.findByCreatedBy(user).stream()
        .map(
            url -> {
              List<AccessLog> logs = accessLogsDAO.findByUrl(url);
              int totalAccesses = logs.size();
              int uniqueVisitors =
                  (int) logs.stream().map(AccessLog::getIpAddress).distinct().count();
              Date lastAccess =
                  logs.stream().map(AccessLog::getAccessedAt).max(Date::compareTo).orElse(null);
              List<String> userAgents = logs.stream().map(AccessLog::getBrowser).toList();
              List<String> operatingSystems = logs.stream().map(AccessLog::getOs).toList();

              UrlStatsDTO stats =
                  new UrlStatsDTO(
                      url.getId(),
                      url.getOriginalUrl(),
                      url.getShortenedUrl(),
                      url.getCreatedAt(),
                      url.getClickCount(),
                      totalAccesses,
                      uniqueVisitors,
                      lastAccess,
                      userAgents,
                      operatingSystems);

              String preview =
                  linkPreviewDAO.findFirstByUrl(url).map(LinkPreview::getPreviewImage).orElse("");

              return new UrlCreatedFullDTO(
                  url.getOriginalUrl(), url.getShortenedUrl(), url.getCreatedAt(), stats, preview);
            })
        .toList();
  }
}
