package org.wornux.urlshortener.util;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvReader {
  private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

  public static String get(String key, String defaultValue) {
    return dotenv.get(key, defaultValue);
  }

  public static String get(String key) {
    return get(key, null);
  }

  public static int getInt(String key, int defaultValue) {
    try {
      return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
