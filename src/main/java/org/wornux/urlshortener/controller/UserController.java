package org.wornux.urlshortener.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.wornux.urlshortener.core.routing.annotations.CONTROLLER;
import org.wornux.urlshortener.core.routing.annotations.GET;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.enums.Routes;
import org.wornux.urlshortener.enums.SessionKeys;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UserService;

/** UserController is a controller class that handles HTTP requests related to users. */
@CONTROLLER(path = "/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Handles the GET request to retrieve all users.
   *
   * @param ctx The context of the HTTP request.
   */
  @GET(path = "/")
  public void getUsers(@Nonnull Context ctx) {
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
            put("users", userService.getUsers());
          }
        };
    ctx.render("pages/users.html", model);
  }

  @GET(path = "/toggleUserRole/{username}")
  public void toggleUserRole(Context ctx) {
    String username = ctx.pathParam("username");
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());

    if (user == null) {
      ctx.status(HttpStatus.UNAUTHORIZED.getCode()).redirect(Routes.USER_LOGIN.getRoute());
      return;
    } else if (!user.getRole().equals(Role.ADMIN)) {
      ctx.status(HttpStatus.FORBIDDEN.getCode());
      return;
    }
    Optional<User> target = userService.getUserByUsername(username);
    target.ifPresentOrElse(
        u -> {
          if (u.getRole().equals(Role.ADMIN)) u.setRole(Role.USER);
          else u.setRole(Role.ADMIN);
          userService.updateUser(u);
          ctx.status(HttpStatus.OK.getCode()).redirect(Routes.USER_LIST.getRoute());
        },
        () -> {
          ctx.status(HttpStatus.NOT_FOUND.getCode()).redirect(Routes.USER_LIST.getRoute());
          return;
        });
  }

  @GET(path = "/toggleUserStatus/{username}")
  public void toggleUserStatus(Context ctx) {
    String username = ctx.pathParam("username");
    User user = ctx.sessionAttribute(SessionKeys.USER.getKey());

    if (user == null) {
      ctx.status(HttpStatus.UNAUTHORIZED.getCode()).redirect(Routes.USER_LOGIN.getRoute());
      return;
    } else if (!user.getRole().equals(Role.ADMIN)) {
      ctx.status(HttpStatus.FORBIDDEN.getCode());
      return;
    }
    Optional<User> target = userService.getUserByUsername(username);
    target.ifPresentOrElse(
        u -> {
          if (u.isDeleted()) u.setDeleted(false);
          else u.setDeleted(true);

          if (u.getUsername().equals("admin")) {
            ctx.status(HttpStatus.FORBIDDEN.getCode());
            return;
          }
          userService.updateUser(u);
          ctx.status(HttpStatus.OK.getCode()).redirect(Routes.USER_LIST.getRoute());
        },
        () -> {
          ctx.status(HttpStatus.NOT_FOUND.getCode()).redirect(Routes.USER_LIST.getRoute());
          return;
        });
  }
}
