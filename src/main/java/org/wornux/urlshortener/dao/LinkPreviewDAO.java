package org.wornux.urlshortener.dao;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.dao.base.BaseDAO;
import org.wornux.urlshortener.model.LinkPreview;
import org.wornux.urlshortener.model.Url;

public class LinkPreviewDAO extends BaseDAO<LinkPreview, ObjectId> {

  public LinkPreviewDAO(Datastore datastore) {
    super(datastore, LinkPreview.class);
  }

  public List<LinkPreview> findByUrl(Url url) {
    return datastore.find(LinkPreview.class).filter(Filters.eq("url", url)).iterator().toList();
  }

  public Optional<LinkPreview> findFirstByUrl(Url url) {
    return findByUrl(url).stream().findFirst();
  }
}
