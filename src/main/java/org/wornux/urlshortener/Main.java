/**
 * Copyright (c) 2025 Wornux. This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package org.wornux.urlshortener;

import io.javalin.Javalin;
import org.wornux.urlshortener.api.grpc.v1.GrpcServer;
import org.wornux.urlshortener.config.AppConfig;
import org.wornux.urlshortener.config.DataSeeder;
import org.wornux.urlshortener.util.EnvReader;

public class Main {
  public static void main(String[] args) {
    // Ensure that the admin user exists in the database.
    DataSeeder.ensureAdmin();

    // Starts a new thread to initialize and run the REST API router.
    new Thread(org.wornux.urlshortener.api.rest.Router::start).start();

    int PORT = EnvReader.getInt("PORT", 7_0_0_0);
    Javalin app = Javalin.create(config -> AppConfig.configureApp(config)).start(PORT);
    AppConfig.ConfigureExceptionHandlers(app);

    new Thread(
            () -> {
              try {
                int GRPC_PORT = EnvReader.getInt("PORT_GRPC", 9090);
                System.out.println("🚀 Servidor gRPC corriendo en el puerto " + GRPC_PORT);
                GrpcServer.start(GRPC_PORT);
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .start();
  }
}
