package org.wornux.urlshortener.enums;

public enum DatastoreConstants {
  MONGO_DATABASE_NAME("urlshortener");

  private final String value;

  DatastoreConstants(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
