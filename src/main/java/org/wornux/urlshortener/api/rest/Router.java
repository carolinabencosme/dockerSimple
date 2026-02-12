package org.wornux.urlshortener.api.rest;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import org.wornux.urlshortener.api.rest.security.AuthMiddleware;
import org.wornux.urlshortener.core.routing.DIContainer;
import org.wornux.urlshortener.util.EnvReader;

public class Router {

  public static void start() {
    int PORT = EnvReader.getInt("PORT-REST", 7_0_0_0);

    Javalin app =
        Javalin.create(
                config -> {
                  config.registerPlugin(
                      new OpenApiPlugin(
                          pluginConfig -> {
                            pluginConfig.withDefinitionConfiguration(
                                (version, definition) -> {
                                  definition.withOpenApiInfo(
                                      info -> info.setTitle("Javalin OpenAPI example"));
                                });
                          }));
                  config.registerPlugin(new SwaggerPlugin());
                  config.registerPlugin(new ReDocPlugin());
                  //

                  config
                      .router
                      .mount(
                          router -> {
                            router.beforeMatched(new AuthMiddleware());
                            AuthController authController = DIContainer.get(AuthController.class);
                            UserController userController = DIContainer.get(UserController.class);
                            UrlController urlController = DIContainer.get(UrlController.class);
                            authController.applyRoutes(router);
                            userController.applyRoutes(router);
                            urlController.applyRoutes(router);
                          })
                      .apiBuilder(() -> {});
                })
            .start(PORT);
  }
}
