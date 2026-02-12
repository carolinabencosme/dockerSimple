package org.wornux.urlshortener.config;

import java.util.Optional;
import org.wornux.urlshortener.core.routing.DIContainer;
import org.wornux.urlshortener.dao.UserDAO;
import org.wornux.urlshortener.enums.Role;
import org.wornux.urlshortener.model.User;

public class DataSeeder {
  public static void ensureAdmin() {
    UserDAO userDAO = DIContainer.get(UserDAO.class);
    Optional<User> adminUser = userDAO.findByUsername("admin");
    if (adminUser.isEmpty()) {
      User user = new User("admin", "admin", "admin@wornux.com", Role.ADMIN);
      userDAO.save(user);
    }
  }
}
