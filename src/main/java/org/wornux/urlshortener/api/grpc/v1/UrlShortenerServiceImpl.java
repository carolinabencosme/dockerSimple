package org.wornux.urlshortener.api.grpc.v1;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.wornux.urlshortener.dto.UrlCreatedFullDTO;
import org.wornux.urlshortener.dto.UrlDTO;
import org.wornux.urlshortener.dto.UrlResponseDTO;
import org.wornux.urlshortener.dto.AnalyticsDTO;
import org.wornux.urlshortener.model.User;
import org.wornux.urlshortener.service.UrlService;
import org.wornux.urlshortener.service.UserService;

@RequiredArgsConstructor
public class UrlShortenerServiceImpl extends UrlShortenerServiceGrpc.UrlShortenerServiceImplBase {

    private final UrlService urlService;
    private final UserService userService;

    @Override
    public void listUserUrls(ListUserUrlsRequest request, StreamObserver<ListUserUrlsResponse> responseObserver) {
        try {
            // Parse and validate user ID
            ObjectId userId = new ObjectId(request.getUserId());
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Lógica extraída de tu UrlController.getUrlsByUser
            var urlDtos = urlService.getUrlsByUser(user);

            // Construir respuesta gRPC
            ListUserUrlsResponse.Builder respBuilder = ListUserUrlsResponse.newBuilder();
            for (UrlResponseDTO dto : urlDtos) {
                UrlData.Builder ud = UrlData.newBuilder()
                        .setOriginalUrl(dto.originalUrl())
                        .setShortenedUrl(dto.shortenedUrl())
                        .setCreatedBy(dto.createdBy())
                        .setCreatedAt(dto.createdAt());

                for (AnalyticsDTO a : dto.analytics()) {
                    ud.addAnalytics(
                            Analytics.newBuilder()
                                    .setAccessDate(a.accessDate())
                                    .setBrowser(a.browser())
                                    .setIpAdress(a.ipAdress())
                                    .setOs(a.os())
                                    .build()
                    );
                }

                respBuilder.addUrls(ud.build());
            }

            responseObserver.onNext(respBuilder.build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Error interno: " + e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void createUrl(CreateUrlRequest request, StreamObserver<CreateUrlResponse> responseObserver) {
        try {
            // Validar userId y obtener User
            ObjectId userId = new ObjectId(request.getUserId());
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Crear la URL usando UrlDTO (la lógica de tu controller)
            UrlDTO urlDto = new UrlDTO(request.getOriginalUrl(), user);
            UrlCreatedFullDTO created = urlService.createFullUrlRecord(urlDto);

            // Formatear fecha al mismo patrón que Javalin
            String createdAt = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    .format(created.createdAt());

            // Construir mensaje gRPC con la respuesta completa
            UrlData.Builder ud = UrlData.newBuilder()
                    .setOriginalUrl(created.originalUrl())
                    .setShortenedUrl(created.shortenedUrl())
                    .setCreatedBy(user.getUsername())
                    .setCreatedAt(createdAt);

            // analytics inicialmente vacío (no hay accesos aún)

            CreateUrlResponse response = CreateUrlResponse.newBuilder()
                    .setUrl(ud.build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Error al crear URL completa: " + e.getMessage()).asRuntimeException()
            );
        }
    }
}
