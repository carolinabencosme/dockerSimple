package org.wornux.urlshortener.util;

import java.util.UUID;

public class UrlShortener {

  public static String generateShortUrl() {
    String shortUrl = UUID.randomUUID().toString().substring(0, 8);
    return shortUrl;
  }
}
