/**
 * Copyright (c) 2025 Wornux This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package org.wornux.urlshortener.model;

import dev.morphia.annotations.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@Entity("shortened_urls")
@Indexes(@Index(fields = @Field("shortenedUrl")))
public class Url {
  @Id private ObjectId id;
  private String originalUrl;
  private String shortenedUrl;
  @Reference private User createdBy;
  private String sessionId;
  private Date createdAt;
  private int clickCount;
  private boolean isOffensive;
  private byte[] qrCode;

  public Url(String originalUrl, String shortenedUrl, User createdBy, byte[] qrCode) {
    this.originalUrl = originalUrl;
    this.shortenedUrl = shortenedUrl;
    this.createdBy = createdBy;
    this.clickCount = 0;
    this.qrCode = qrCode;
    this.isOffensive = false;
  }

  @PrePersist
  public void prePersist() {
    if (createdAt == null) {
      createdAt = new Date();
    }
  }

  public Url(String originalUrl2, String shortUrl, String sessionId, byte[] qrCode2) {
    this.originalUrl = originalUrl2;
    this.shortenedUrl = shortUrl;
    this.sessionId = sessionId;
    this.qrCode = qrCode2;
  }
}
