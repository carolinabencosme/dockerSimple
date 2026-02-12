package org.wornux.urlshortener.controller;

import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;
import org.wornux.urlshortener.core.routing.annotations.CONTROLLER;
import org.wornux.urlshortener.core.routing.annotations.GET;
import org.wornux.urlshortener.core.routing.annotations.POST;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.enums.Routes;
import org.wornux.urlshortener.enums.SessionKeys;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UrlService;
import org.wornux.urlshortener.service.UserService;

/**
 * AuthController is a controller class that handles HTTP requests related to authentication and
 * authorization
 */
@CONTROLLER(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final UrlService urlService;

  /**
   * Handles the GET request to render the login page.
   *
   * @param ctx The context of the HTTP request.
   */
  @GET(path = "/login")
  public void renderLoginPage(Context ctx) {
    ctx.render("/auth/login.html");
  }

  /**
   * Handles the POST request to authenticate a user.
   *
   * @param ctx The context of the HTTP request.
   */
  @GET(path = "/signup")
  public void renderSignupPage(Context ctx) {
    ctx.render("/auth/signup.html");
  }

  /**
   * Handles the POST request to authenticate a user.
   *
   * @param ctx The context of the HTTP request.
   */
  @POST(path = "/login")
  public void login(Context ctx) {
    String username = ctx.formParam("username");
    String password = ctx.formParam("password");

    userService
        .authenticate(username, password)
        .ifPresentOrElse(
            user -> {
              if (!user.isDeleted()) {
                ctx.sessionAttribute(SessionKeys.USER.getKey(), user);
                urlService.migrateSessionUrlsToUser(ctx.req().getSession().getId(), user);
                ctx.redirect(Routes.HOME.getRoute());
                return;
              }
              ctx.redirect(Routes.USER_LOGIN.getRoute());
            },
            () -> {
              System.out.println("❌ Error: Usuario no autenticado.");
              ctx.redirect(Routes.USER_LOGIN.getRoute());
            });
  }

  /**
   * Handles the GET request to logout a user.
   *
   * @param ctx The context of the HTTP request.
   */
  @GET(path = "/logout")
  public void logout(Context ctx) {
    ctx.req().getSession().invalidate();
    ctx.redirect(Routes.HOME.getRoute());
  }

  /**
   * Handles the POST request to register a new user.
   *
   * @param ctx The context of the HTTP request.
   */
  @POST(path = "/signup")
  public void signup(Context ctx) {
    String username = ctx.formParam("username");
    String email = ctx.formParam("email");
    String password = ctx.formParam("password");

    Role role = Role.USER;

    User newUser = new User(username, password, email, role);

    try {
      userService.saveUser(newUser);
      urlService.migrateSessionUrlsToUser(ctx.req().getSession().getId(), newUser);
      ctx.sessionAttribute(SessionKeys.USER.getKey(), newUser);
      ctx.redirect(Routes.HOME.getRoute());
    } catch (IllegalArgumentException e) {
      System.out.println("❌ Error al registrar el usuario: " + e.getMessage());
      ctx.redirect(Routes.USER_REGISTER.getRoute());
    }
  }
}
