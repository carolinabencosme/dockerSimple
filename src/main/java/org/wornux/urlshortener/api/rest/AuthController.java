package org.wornux.urlshortener.api.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import io.javalin.router.JavalinDefaultRouting;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.wornux.urlshortener.api.rest.security.JwtUtil;
import org.wornux.urlshortener.dto.Authentication;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UserService;

@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  public void applyRoutes(JavalinDefaultRouting router) {
    router.post("/auth/login", this::loginPost);
  }

  @OpenApi(
      path = "/auth/login",
      methods = {HttpMethod.POST},
      summary = "Autenticar usuario",
      requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Authentication.class)}),
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = JWTResponse.class)}),
        @OpenApiResponse(status = "401", description = "Credenciales incorrectas")
      })
  public void loginPost(Context ctx) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(ctx.body());

      String username = jsonNode.get("username").asText();
      String password = jsonNode.get("password").asText();

      System.out.println("🟡 Intentando autenticación");
      System.out.println("➡️ Usuario: " + username);
      System.out.println("➡️ Clave: " + password);

      Optional<User> optionalUser = userService.authenticate(username, password);

      if (optionalUser.isPresent()) {
        User user = optionalUser.get();

        System.out.println("✅ Usuario autenticado: " + user.getUsername());

        JwtUtil jwtUtil = new JwtUtil();
        String token =
            jwtUtil.generateToken(
                Map.of("user_id", user.getId().toHexString()), // 👈 se agrega aquí
                new Authentication(user.getUsername(), user.getPassword()));

        ctx.json(new JWTResponse(token));
        System.out.println("🔐 Token generado exitosamente");
      } else {
        System.out.println("❌ Credenciales incorrectas");
        ctx.status(401).result("Credenciales incorrectas o usuario no encontrado");
      }

    } catch (Exception e) {
      System.out.println("❗ ERROR durante login:");
      e.printStackTrace(); // ← esto mostrará la línea exacta del error
      ctx.status(500).result("Ocurrió un error inesperado");
    }
  }
  ;

  public class JWTResponse {
    public String token;

    public JWTResponse(String token) {
      this.token = token;
    }
  }
}
