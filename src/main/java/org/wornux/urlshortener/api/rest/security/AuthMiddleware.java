package org.wornux.urlshortener.api.rest.security;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.Header;
import io.javalin.http.UnauthorizedResponse;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.wornux.urlshortener.core.routing.DIContainer;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UserService;

public class AuthMiddleware implements Handler {

  private final JwtUtil jwtUtil = new JwtUtil();

  public List<Role> getUserRoles(Context ctx) {
    String authHeader = ctx.header(Header.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return List.of(); // no autorizado
    }

    String token = authHeader.substring(7); // quitar "Bearer "

    if (!jwtUtil.validateToken(token)) {
      return List.of(); // token inválido
    }

    String username = jwtUtil.getUsernameFromToken(token);

    Optional<User> userOpt = DIContainer.get(UserService.class).getUserByUsername(username);

    return userOpt.map(user -> List.of(user.getRole())).orElse(List.of());
  }

  @Override
  public void handle(@NotNull Context ctx) throws Exception {
    var permittedRoles = ctx.routeRoles();

    if (ctx.path().startsWith("/swagger")
        || ctx.path().startsWith("/openapi")
        || ctx.path().startsWith("/redoc")
        || ctx.path().startsWith("/webjars")
        || ctx.path().equals("/auth/login")) {
      return;
    }

    if (permittedRoles.contains(Role.ANYONE)) return;

    if (getUserRoles(ctx).stream().anyMatch(permittedRoles::contains)) return;

    ctx.header(Header.WWW_AUTHENTICATE, "Bearer");
    throw new UnauthorizedResponse("No autorizado");
  }
}
