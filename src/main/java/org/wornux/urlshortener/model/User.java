package org.wornux.urlshortener.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PrePersist;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.enums.Role;

@Entity("users")
@Getter
@Setter
@NoArgsConstructor
public class User {
  @Id private ObjectId id;
  private String username;
  private String password;
  private String email;
  private Role role;
  private Date createdAt;
  private Date updatedAt;
  private boolean isDeleted;

  public User(String username, String password, String email, Role role) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.isDeleted = false;
  }

  @PrePersist
  public void PrePersist() {
    if (createdAt == null) {
      createdAt = new Date();
    }
    updatedAt = new Date();
  }
}
