package org.wornux.urlshortener.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.Optional;
import org.wornux.urlshortener.core.routing.annotations.CONTROLLER;
import org.wornux.urlshortener.core.routing.annotations.GET;
import org.wornux.urlshortener.dto.AccessLogDTO;
import org.wornux.urlshortener.model.Url;
import org.wornux.urlshortener.service.AccessLogsService;
import org.wornux.urlshortener.service.UrlService;
import org.wornux.urlshortener.util.UserAgentParser;

@CONTROLLER(path = "/s")
public class RedirectController {
  private final UrlService urlService;
  private final AccessLogsService accessLogService;

  /**
   * Constructor to initialize the RedirectController with the required service.
   *
   * @param urlService Service for managing shortened URLs.
   */
  public RedirectController(UrlService urlService, AccessLogsService accessLogService) {
    this.urlService = urlService;
    this.accessLogService = accessLogService;
  }

  /**
   * Handles GET requests to redirect to the original URL.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/{shortUrl}")
  public void redirectToOriginalUrl(Context ctx) {
    String shortUrl = ctx.pathParam("shortUrl");
    Optional<Url> shortenedUrl = urlService.getShortenedUrlByHash(shortUrl);
    if (shortenedUrl.isPresent()) {
      if (shortenedUrl.get().isOffensive()) {
        ctx.status(HttpStatus.NOT_FOUND.getCode());
        return;
      }

      String userAgent = ctx.userAgent();
      String ipAddress = ctx.ip();
      String browser = UserAgentParser.getBrowser(userAgent);
      String operatingSystem = UserAgentParser.getOperatingSystem(userAgent);

      AccessLogDTO accessLogDTO =
          new AccessLogDTO(
              shortenedUrl.get().getShortenedUrl(),
              shortenedUrl.get().getCreatedAt(),
              browser,
              ipAddress,
              operatingSystem);

      accessLogService.createAccessLog(accessLogDTO);
      ctx.redirect(shortenedUrl.get().getOriginalUrl());
    } else {
      ctx.status(404).result("Shortened URL not found");
    }
  }
}
