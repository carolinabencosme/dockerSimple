package org.wornux.urlshortener.dao;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import java.util.List;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.dao.base.BaseDAO;
import org.wornux.urlshortener.model.AccessLog;
import org.wornux.urlshortener.model.Url;

public class AccessLogsDAO extends BaseDAO<AccessLog, ObjectId> {

  public AccessLogsDAO(Datastore datastore) {
    super(datastore, AccessLog.class);
  }

  public List<AccessLog> findAccessLogsByShortenedUrlId(ObjectId shortenedUrlId) {
    return datastore
        .find(AccessLog.class)
        .filter(Filters.eq("shortenedUrl", shortenedUrlId))
        .iterator()
        .toList();
  }

  public List<AccessLog> findByUrl(Url url) {
    return datastore.find(AccessLog.class).filter(Filters.eq("url", url)).iterator().toList();
  }
}
