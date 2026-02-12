package org.wornux.urlshortener.model;

import dev.morphia.annotations.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Entity("link_previews")
@Getter
@Setter
@NoArgsConstructor
public class LinkPreview {
  @Id private ObjectId id;
  @Reference private Url url;
  private String previewImage;
  private Date createdAt;

  public LinkPreview(Url url, String previewImage) {
    this.url = url;
    this.previewImage = previewImage;
  }

  @PrePersist
  public void prePersist() {
    if (createdAt == null) {
      createdAt = new Date();
    }
  }
}
