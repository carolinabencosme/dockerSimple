package org.wornux.urlshortener.controller.base;

import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseController serves as an abstract base class for all controllers in the application. It
 * provides common functionality and structure for derived controllers.
 */
public abstract class BaseController {
  /*
   * Creates a model map from the provided key-value pairs.
   *
   * @param args A variable-length argument list where arguments should be in
   * pairs (key, value).
   * Keys must be Strings, and values can be any Object.
   *
   * @return A Map<String, Object> containing the key-value pairs.
   *
   * @throws IllegalArgumentException If the number of arguments is not even or if
   * a key is not a String.
   */

  protected Javalin app;

  public BaseController(Javalin app) {
    this.app = app;
  }

  public abstract void applyRoutes();

  public static Map<String, Object> setModel(Object... args) {
    Map<String, Object> model = new HashMap<>();
    if (args.length % 2 != 0) {
      throw new IllegalArgumentException(
          "Invalid number of arguments. Arguments should be in pairs (key, value).");
    }
    for (int i = 0; i < args.length; i += 2) {
      if (!(args[i] instanceof String)) {
        throw new IllegalArgumentException(
            "Argument at position " + i + " is not a String. Keys should be Strings.");
      }
      model.put((String) args[i], args[i + 1]);
    }
    return model;
  }
}
