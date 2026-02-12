package org.wornux.urlshortener.enums;

import io.javalin.security.RouteRole;

/** Represents the roles available in the URL shortener application. */
public enum Role implements RouteRole {
  ADMIN,
  USER,
  USER_READ,
  USER_WRITE,
  ANYONE;
}
