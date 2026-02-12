package org.wornux.urlshortener.core.routing;

import io.javalin.http.Context;
import io.javalin.router.JavalinDefaultRouting;
import java.lang.reflect.Method;
import java.util.List;
import org.wornux.urlshortener.core.routing.annotations.CONTROLLER;
import org.wornux.urlshortener.core.routing.annotations.DELETE;
import org.wornux.urlshortener.core.routing.annotations.GET;
import org.wornux.urlshortener.core.routing.annotations.POST;
import org.wornux.urlshortener.core.routing.annotations.PUT;

public class Router {

  public static void registerRoutes(JavalinDefaultRouting router) {
    List<Class<?>> controllers = ReflectionPaths.getClasses("org.wornux.urlshortener.controller");

    for (Class<?> clazz : controllers) {
      if (clazz.isAnnotationPresent(CONTROLLER.class)) {
        CONTROLLER controller = clazz.getAnnotation(CONTROLLER.class);

        try {
          // Obtener la instancia del controlador a través del contenedor de DI
          Object controllerInstance = DIContainer.get(clazz);

          for (Method method : clazz.getDeclaredMethods()) {
            String path = controller.path();

            if (method.isAnnotationPresent(GET.class)) {
              GET get = method.getAnnotation(GET.class);
              path += get.path();
              router.get(path, ctx -> invokeMethod(method, controllerInstance, ctx));
            } else if (method.isAnnotationPresent(POST.class)) {
              POST post = method.getAnnotation(POST.class);
              path += post.path();
              router.post(path, ctx -> invokeMethod(method, controllerInstance, ctx));
            } else if (method.isAnnotationPresent(PUT.class)) {
              PUT put = method.getAnnotation(PUT.class);
              path += put.path();
              router.put(path, ctx -> invokeMethod(method, controllerInstance, ctx));
            } else if (method.isAnnotationPresent(DELETE.class)) {
              DELETE delete = method.getAnnotation(DELETE.class);
              path += delete.path();
              router.delete(path, ctx -> invokeMethod(method, controllerInstance, ctx));
            }
          }
        } catch (Exception e) {
          System.err.println("❌ Error initializing controller: " + clazz.getName());
          e.printStackTrace();
        }
      } else {
        System.out.println("⏩ Skipping non-controller class: " + clazz.getName());
      }
    }
  }

  private static void invokeMethod(Method method, Object instance, Context ctx) {
    try {
      if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Context.class) {
        method.invoke(instance, ctx);
      } else {
        System.err.println(
            "❌ Invalid method signature: " + method.getName() + " must accept only Context.");
      }
    } catch (Exception e) {
      System.err.println("❌ Error invoking method: " + method.getName());
      e.printStackTrace();
    }
  }
}
