package org.wornux.urlshortener.api.rest;

import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.wornux.urlshortener.api.rest.security.JwtUtil;

public class JwtMiddleware {

  public static Handler jwtAuth =
      ctx -> {
        String token = ctx.header("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
          ctx.status(HttpStatus.UNAUTHORIZED).result("No token provided");
          return;
        }

        token = token.substring(7); // Eliminar el "Bearer "

        JwtUtil jwtUtil = new JwtUtil();
        try {
          if (!jwtUtil.validateToken(token)) {
            ctx.status(HttpStatus.UNAUTHORIZED).result("Invalid or expired token");
            return;
          }
          String username = jwtUtil.getUsernameFromToken(token);
          ctx.attribute("user", username); // Guardar el usuario en el contexto
        } catch (Exception e) {
          ctx.status(HttpStatus.UNAUTHORIZED).result(e.getMessage());
        }
      };
}
