package org.wornux.urlshortener.core.routing;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import java.util.Optional;
import java.util.logging.Logger;
import org.wornux.urlshortener.enums.DatastoreConstants;
import org.wornux.urlshortener.util.EnvReader;

public class DatastoreProvider {
  private static Datastore datastore;

  public static synchronized Datastore getDatastore() {
    if (datastore == null) {
      String connectionString =
          Optional.ofNullable(EnvReader.get("MONGODB_URL"))
              .orElseThrow(
                  () -> {
                    Logger.getLogger(DatastoreProvider.class.getName())
                        .log(
                            java.util.logging.Level.SEVERE,
                            "\u001B[31mMONGODB_URL environment variable not set\u001B[0m");
                    return new RuntimeException("MONGODB_URL environment variable not set");
                  });
      datastore =
          Morphia.createDatastore(
              MongoClients.create(connectionString),
              DatastoreConstants.MONGO_DATABASE_NAME.getValue());
      datastore.getMapper().mapPackage("org.wornux.urlshortener.model");
      datastore.ensureIndexes();
    }
    return datastore;
  }
}
