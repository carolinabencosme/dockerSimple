package org.wornux.urlshortener.core.routing;

import dev.morphia.Datastore;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {

  private static final Map<Class<?>, Object> instances = new HashMap<>();

  // Crear una instancia de la clase y resolver sus dependencias
  @SuppressWarnings("unchecked")
  public static <T> T get(Class<T> clazz) {
    // Si ya se ha instanciado la clase, devolver la instancia existente
    if (instances.containsKey(clazz)) {
      return (T) instances.get(clazz);
    }

    try {
      // Obtener el constructor de la clase
      Constructor<?>[] constructors = clazz.getDeclaredConstructors();
      for (Constructor<?> constructor : constructors) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        // Resolver cada dependencia del constructor
        for (int i = 0; i < paramTypes.length; i++) {
          if (paramTypes[i].isPrimitive()) {
            throw new RuntimeException("No se puede resolver una dependencia primitiva");
          } else if (paramTypes[i] == Datastore.class) {
            // Si la dependencia es EntityManager, obtenerlo desde el proveedor
            params[i] = DatastoreProvider.getDatastore();
          } else {
            // Resolver recursivamente otras dependencias
            params[i] = get(paramTypes[i]);
          }
        }

        // Crear la instancia con las dependencias resueltas
        constructor.setAccessible(true);
        Object instance = constructor.newInstance(params);

        // Guardar la instancia para futuras inyecciones
        instances.put(clazz, instance);
        return (T) instance;
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al crear la instancia de " + clazz.getName(), e);
    }

    // Si no se encontró un constructor válido
    throw new RuntimeException("No se encontró un constructor válido para " + clazz.getName());
  }
}
