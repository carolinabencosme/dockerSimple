package org.wornux.urlshortener.config;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.wornux.urlshortener.core.routing.Router;
import org.wornux.urlshortener.security.AuthMiddleware;

public class AppConfig {
  public static void configureApp(JavalinConfig config) {
    config.staticFiles.add(
        stc -> {
          stc.hostedPath = "/";
          stc.directory = "/public";
        });
    config.fileRenderer(templateEngineConfig());
    config.router.mount(
        router -> {
          router.get("/", ctx -> ctx.redirect("shortened"));
          Router.registerRoutes(router);
          router.beforeMatched(new AuthMiddleware());
        });
  }

  public static JavalinThymeleaf templateEngineConfig() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("/templates/");

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    JavalinThymeleaf thymeleafRenderer = new JavalinThymeleaf(templateEngine);
    return thymeleafRenderer;
  }

  public static void ConfigureExceptionHandlers(Javalin app) {
    app.error(
        HttpStatus.NOT_FOUND.getCode(),
        ctx -> {
          ctx.render("pages/404.html");
        });
    app.error(
        HttpStatus.FORBIDDEN.getCode(),
        ctx -> {
          ctx.render("pages/403.html");
        });
  }
}
