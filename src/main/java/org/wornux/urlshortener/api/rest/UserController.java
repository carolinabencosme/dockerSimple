package org.wornux.urlshortener.api.rest;

import io.javalin.http.Context;
import io.javalin.router.JavalinDefaultRouting;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UserService;

// @CONTROLLER(path = "/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  public void applyRoutes(JavalinDefaultRouting router) {
    router.get("/users/", this::getAllUsers, Role.ADMIN, Role.USER_READ);
    router.post("/users/", this::createUser, Role.ADMIN, Role.USER_WRITE);
    router.get("/users/{userId}", this::getUser, Role.ADMIN, Role.USER_READ);
    router.put("/users/{userId}", this::updateUser, Role.ADMIN, Role.USER_WRITE);
    router.delete("/users/{userId}", this::deleteUser, Role.ADMIN, Role.ADMIN);
  }

  // GET /users/ --> Lista todos los usuarios
  // @GET(path = "/")
  public void getAllUsers(@Nonnull Context ctx) {
    List<User> users = userService.getUsers();
    ctx.json(users);
  }

  // POST /users/ --> Crea un nuevo usuario
  // @POST(path = "/")
  public void createUser(@Nonnull Context ctx) {
    try {
      // Se espera que el cuerpo de la petición contenga un JSON que se mapea a User
      User newUser = ctx.bodyAsClass(User.class);
      userService.saveUser(newUser);
      ctx.status(201).json(newUser);
    } catch (Exception e) {
      ctx.status(400).result("Solicitud inválida: " + e.getMessage());
    }
  }

  // GET /users/{userId} --> Obtiene un usuario por ID
  // @GET(path = "/{userId}")
  public void getUser(@Nonnull Context ctx) {
    String userIdStr = ctx.pathParam("userId");
    try {
      ObjectId userId = new ObjectId(userIdStr);
      Optional<User> optionalUser = userService.getUserById(userId);
      if (optionalUser.isPresent()) {
        ctx.json(optionalUser.get());
      } else {
        ctx.status(404).result("Usuario no encontrado");
      }
    } catch (Exception e) {
      ctx.status(400).result("ID de usuario inválido");
    }
  }

  // PUT /users/{userId} --> Actualiza un usuario existente
  // @PUT(path = "/{userId}")
  public void updateUser(@Nonnull Context ctx) {
    String userIdStr = ctx.pathParam("userId");
    try {
      ObjectId userId = new ObjectId(userIdStr);
      Optional<User> optionalUser = userService.getUserById(userId);
      if (optionalUser.isPresent()) {
        // Mapea el cuerpo de la solicitud a un objeto User
        User updatedUser = ctx.bodyAsClass(User.class);
        // Asegurarse de que el ID coincida
        updatedUser.setId(userId);
        userService.updateUser(updatedUser);
        ctx.json(updatedUser);
      } else {
        ctx.status(404).result("Usuario no encontrado");
      }
    } catch (Exception e) {
      ctx.status(400).result("Solicitud inválida: " + e.getMessage());
    }
  }

  // DELETE /users/{userId} --> Elimina un usuario
  // @DELETE(path = "/{userId}")
  public void deleteUser(@Nonnull Context ctx) {
    String userIdStr = ctx.pathParam("userId");
    try {
      ObjectId userId = new ObjectId(userIdStr);
      userService.deleteUser(userId);
      ctx.status(204); // No Content
    } catch (Exception e) {
      ctx.status(400).result("ID de usuario inválido");
    }
  }
}
