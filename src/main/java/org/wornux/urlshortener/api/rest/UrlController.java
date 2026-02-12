package org.wornux.urlshortener.api.rest;

import io.javalin.http.Context;
import io.javalin.router.JavalinDefaultRouting;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.dto.UrlCreatedFullDTO;
import org.wornux.urlshortener.dto.UrlDTO;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UrlService;
import org.wornux.urlshortener.service.UserService;

@RequiredArgsConstructor
public class UrlController {
  private final UrlService urlService;
  private final UserService userService;

  public void applyRoutes(JavalinDefaultRouting router) {
    router.get("/users/{userId}/urls", this::getUrlsByUser, Role.ADMIN, Role.USER);
    router.post("/urls/", this::createFullUrlRecord, Role.ADMIN, Role.USER);
  }

  public void getUrlsByUser(@Nonnull Context ctx) {
    try {
      ObjectId userId = new ObjectId(ctx.pathParam("userId"));
      Optional<User> userOpt = userService.getUserById(userId);
      if (userOpt.isPresent()) {
        ctx.json(urlService.getUrlsByUser(userOpt.get()));
        return;
      } else {
        ctx.status(404).result("Usuario no encontrado");
      }
    } catch (Exception e) {
      ctx.status(400).result("ID de usuario inválido");
    }
  }

  public void createFullUrlRecord(@Nonnull Context ctx) {
    try {
      UrlDTO urlDTO = ctx.bodyAsClass(UrlDTO.class);
      UrlCreatedFullDTO response = urlService.createFullUrlRecord(urlDTO);
      ctx.status(201).json(response);
    } catch (Exception e) {
      ctx.status(400).result("Error al crear URL completa: " + e.getMessage());
    }
  }
}
