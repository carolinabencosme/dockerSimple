package org.wornux.urlshortener.dao;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.dao.base.BaseDAO;
import org.wornux.urlshortener.model.User;

public class UserDAO extends BaseDAO<User, ObjectId> {
  public UserDAO(@Nonnull Datastore datastore) {
    super(datastore, User.class);
  }

  public Optional<User> findByEmailAndPassword(String email, String password) {
    return Optional.ofNullable(
        datastore
            .find(User.class)
            .filter(Filters.eq("email", email))
            .filter(Filters.eq("password", password))
            .first());
  }

  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(
        datastore.find(User.class).filter(Filters.eq("username", username)).first());
  }

  public Optional<User> findByUsernameAndPassword(String username, String password) {
    return Optional.ofNullable(
        datastore
            .find(User.class)
            .filter(Filters.eq("username", username))
            .filter(Filters.eq("password", password))
            .first());
  }
}
