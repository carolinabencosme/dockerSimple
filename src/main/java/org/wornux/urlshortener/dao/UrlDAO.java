package org.wornux.urlshortener.dao;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.wornux.urlshortener.dao.base.BaseDAO;
import org.wornux.urlshortener.model.Url;
import org.wornux.urlshortener.model.User;

public class UrlDAO extends BaseDAO<Url, ObjectId> {

  public UrlDAO(Datastore datastore) {
    super(datastore, Url.class);
  }

  public Optional<Url> findByHash(String shortenedUrl) {
    return Optional.ofNullable(
        datastore.find(Url.class).filter(Filters.eq("shortenedUrl", shortenedUrl)).first());
  }

  public List<Url> findByCreatedBy(User user) {
    return datastore.find(Url.class).filter(Filters.eq("createdBy", user)).iterator().toList();
  }

  public void migrateSessionUrlsToUser(String sessionId, User user) {
    datastore
        .find(Url.class)
        .filter(Filters.eq("sessionId", sessionId))
        .update(UpdateOperators.set("createdBy", user))
        .execute();
  }

  public List<Url> findBySession(@NotNull String sessionId) {
    return datastore.find(Url.class).filter(Filters.eq("sessionId", sessionId)).iterator().toList();
  }
}
