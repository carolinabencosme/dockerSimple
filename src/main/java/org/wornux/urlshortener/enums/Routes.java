package org.wornux.urlshortener.enums;

public enum Routes {
  // User routes
  USER_LOGIN("/auth/login"),
  USER_REGISTER("/register"),
  USER_LOGOUT("/logout"),
  USER_LIST("/users"),
  USER_VIEW("/users/{id}"),
  USER_UPDATE("/users/{id}/update"),
  USER_DELETE("/users/{id}/delete"),
  // Redirect routes
  REDIRECT("/r/{id}"),
  HOME("/shortened"),
  URLS_LIST("/shortened/urls");
  // statics

  private final String route;

  Routes(String route) {
    this.route = route;
  }

  public String getRoute() {
    return route;
  }
}
