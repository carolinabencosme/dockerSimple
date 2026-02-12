package org.wornux.urlshortener.model;

import dev.morphia.annotations.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Entity("access_logs")
@Getter
@Setter
@NoArgsConstructor
public class AccessLog {
  @Id private ObjectId id;
  @Reference private Url url;
  private Date accessedAt;
  private String browser;
  private String ipAddress;
  private String os; // Operating System

  public AccessLog(Url url, String browser, String ipAddress, String os) {
    this.url = url;
    this.browser = browser;
    this.ipAddress = ipAddress;
    this.os = os;
  }

  @PrePersist
  public void prePersist() {
    if (accessedAt == null) {
      accessedAt = new Date();
    }
  }
}
