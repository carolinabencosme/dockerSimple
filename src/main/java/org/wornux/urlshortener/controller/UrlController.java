package org.wornux.urlshortener.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.core.routing.annotations.CONTROLLER;
import org.wornux.urlshortener.core.routing.annotations.GET;
import org.wornux.urlshortener.core.routing.annotations.POST;
import org.wornux.urlshortener.dto.UrlCreatedDTO;
import org.wornux.urlshortener.dto.UrlDTO;
import org.wornux.urlshortener.dto.UrlUnknownDTO;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.enums.Routes;
import org.wornux.urlshortener.enums.SessionKeys;
import org.wornux.urlshortener.model.Url;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UrlService;

/** Controller for handling operations related to shortened URLs. */
@CONTROLLER(path = "shortened")
public class UrlController {

  private final UrlService urlService;

  /**
   * Constructor to initialize the UrlController with the required service.
   *
   * @param urlService Service for managing shortened URLs.
   */
  public UrlController(UrlService urlService) {
    this.urlService = urlService;
  }

  /**
   * Handles GET requests to list all shortened URLs.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/")
  public void listShortenedUrls(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    String sessionId = ctx.req().getSession().getId();
    Map<String, Object> model =
        new HashMap<>() {
          {
            put("user", user);
            if (user != null) {
              put("urls", urlService.getShortenedUrlsByUser(user));
            } else put("urls", urlService.getUrlsBySession(sessionId));
          }
        };
    ctx.render("pages/home.html", model);
  }

  /**
   * handles get all urls requests for admins
   *
   * @param ctx Javalin HTTP context
   */
  @GET(path = "/urls")
  public void getAllUrls(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    if (user == null) {
      ctx.redirect(Routes.USER_LOGIN.getRoute());
      return;
    } else if (!user.getRole().equals(Role.ADMIN)) {
      ctx.status(HttpStatus.FORBIDDEN.getCode());
      return;
    }
    Map<String, Object> model =
        new HashMap<>() {
          {
            put("user", user);
            put("urls", urlService.getAllShortenedUrls());
          }
        };
    ctx.render("pages/urls.html", model);
  }

  /**
   * Handles GET requests to display the dashboard for a specific shortened URL.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/dashboard")
  public void dashboard(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    String sessionId = ctx.req().getSession().getId();
    Map<String, Object> model =
        new HashMap<>() {
          {
            put("user", user);
            put("isAdmin", user != null && user.getRole().equals(Role.ADMIN));
            if (user != null) {
              put("urls", urlService.getShortenedUrlsByUser(user));
            } else put("urls", urlService.getUrlsBySession(sessionId));
          }
        };
    ctx.render("pages/dashboard.html", model);
  }

  /**
   * Handles GET requests to render analytics page
   *
   * @param ctx The Javalin HTTP context
   */
  @GET(path = "/analytics")
  public void analytics(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    String sessionId = ctx.req().getSession().getId();

    Map<String, Object> model =
        new HashMap<>() {
          {
            put("user", user);
            if (user != null) {
              put("urls", urlService.getShortenedUrlsByUser(user));
            } else put("urls", urlService.getUrlsBySession(sessionId));
          }
        };
    ctx.render("pages/analytics.html", model);
  }

  /**
   * Handles GET requests to render analytics page for a specific shortened URL by its hash.
   *
   * @param ctx The Javalin HTTP context
   */
  @GET(path = "/analytics/{hash}")
  public void analyticsByHash(Context ctx) {
    String hash = ctx.pathParam("hash");
    Map<String, Object> model =
        new HashMap<>() {
          {
            put("hash", hash);
          }
        };
    ctx.render("pages/analytics.html", model);
  }

  /**
   * Handles toggleOffensiveUrl Requests
   *
   * @param ctx Javalin HTTP context
   */
  @GET(path = "/toggleOffensiveUrl/{hash}")
  public void toggleOffensiveUrl(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    if (user == null) {
      ctx.redirect(Routes.USER_LOGIN.getRoute());
      return;
    } else if (!user.getRole().equals(Role.ADMIN)) {
      ctx.status(HttpStatus.FORBIDDEN.getCode());
      return;
    }
    String hash = ctx.pathParam("hash");
    if (hash.isEmpty() || hash == null) {
      ctx.status(HttpStatus.NOT_FOUND.getCode()).redirect(Routes.URLS_LIST.getRoute());
      return;
    }

    Optional<Url> url = urlService.getShortenedUrlByHash(hash);
    url.ifPresentOrElse(
        u -> {
          if (u.isOffensive()) u.setOffensive(false);
          else u.setOffensive(true);
          urlService.update(u);
          ctx.status(HttpStatus.OK.getCode()).redirect(Routes.URLS_LIST.getRoute());
          return;
        },
        () -> {
          ctx.status(HttpStatus.NOT_FOUND.getCode()).redirect(Routes.URLS_LIST.getRoute());
          return;
        });
  }

  /**
   * Handles GET requests to retrieve analytics data for a specific user.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/analytics_data/")
  public void getAnalyticsData(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    String sessionId = ctx.req().getSession().getId();

    if (user == null) {
      ctx.status(HttpStatus.OK.getCode()).json(urlService.getAnaliticsBySession(sessionId));
      return;
    }
    ctx.status(HttpStatus.OK.getCode()).json(urlService.getAnaliticsByUser(user));
  }

  /**
   * Handles GET requests to retrieve analytics data for a specific shortened URL by its hash.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/analytics_data/{hash}")
  public void getAnalyticsDataByHash(Context ctx) {
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    String sessionId = ctx.req().getSession().getId();
    String hash = ctx.pathParam("hash");
    if (user == null) {
      ctx.status(HttpStatus.OK.getCode())
          .json(urlService.getAnaliticsBySessionAndHash(sessionId, hash));
      return;
    }
    ctx.status(HttpStatus.OK.getCode()).json(urlService.getAnaliticsByUserAndHash(user, hash));
  }

  /**
   * Handles POST requests to create a new shortened URL.
   *
   * @param ctx The Javalin HTTP context.
   */
  @POST(path = "/create")
  public void createShortenedUrl(Context ctx) {
    String url = ctx.formParam("url");
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());
    if (user == null) {
      String sessionId = ctx.req().getSession().getId();
      UrlUnknownDTO urlUnknownDTO = new UrlUnknownDTO(url, sessionId);
      urlService.createShortenedUrl(urlUnknownDTO);
      ctx.redirect("/");
      return;
    }
    UrlDTO urlDTO = new UrlDTO(url, user);
    urlService.createShortenedUrl(urlDTO);

    ctx.redirect("/");
  }

  /**
   * Handles GET requests to delete a specific shortened URL.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/{id}/delete")
  public void deleteShortenedUrl(Context ctx) {
    String id = ctx.pathParam("id");
    urlService.deleteShortenedUrl(new ObjectId(id));
    ctx.redirect("/shortened/dashboard");
  }

  /**
   * Handles GET requests to retrieve the QR code for a specific shortened URL.
   *
   * @param ctx The Javalin HTTP context.
   */
  @GET(path = "/{id}/qr")
  public void getQrCode(Context ctx) {
    String id = ctx.pathParam("id");
    Optional<UrlCreatedDTO> shortenedUrl = urlService.getShortenedUrlById(new ObjectId(id));
    if (shortenedUrl.isEmpty()) {
      ctx.status(404);
      return;
    }
    String base64QRCode = Base64.getEncoder().encodeToString(shortenedUrl.get().qrCode());
    Map<String, Object> model =
        new HashMap<>() {
          {
            put("base64QRCode", base64QRCode);
          }
        };
    ctx.render("pages/qrCode.html", model);
  }
}
