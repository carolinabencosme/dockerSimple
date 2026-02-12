package org.wornux.urlshortener.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LinkPreviewGenerator {

  public static String captureBase64Preview(String websiteUrl) {
    if (websiteUrl == null || websiteUrl.isEmpty()) {
      throw new IllegalArgumentException("URL is required");
    }

    // Tu clave de API de LinkPreview
    String apiKey = "a1953d9b7a1283618650bcfb52dbd0bf"; // Reemplaza con tu clave
    String linkPreviewUrl =
        String.format(
            "https://api.linkpreview.net/?key=%s&q=%s",
            apiKey, URLEncoder.encode(websiteUrl, StandardCharsets.UTF_8));

    try {
      // Crear cliente HTTP
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(linkPreviewUrl)).GET().build();

      // Enviar solicitud a LinkPreview
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new RuntimeException("LinkPreview API error: " + response.body());
      }

      // Parsear la respuesta JSON
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.readTree(response.body());
      String imageUrl = jsonNode.path("image").asText();

      if (imageUrl.isEmpty()) {
        throw new RuntimeException("No preview image available");
      }

      // Descargar la imagen
      HttpRequest imageRequest = HttpRequest.newBuilder().uri(new URI(imageUrl)).GET().build();
      HttpResponse<byte[]> imageResponse =
          client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());

      if (imageResponse.statusCode() != 200) {
        throw new RuntimeException("Failed to download image");
      }

      // Convertir la imagen a Base64
      byte[] imageBytes = imageResponse.body();
      return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

    } catch (Exception e) {
      throw new RuntimeException("Error: " + e.getMessage(), e);
    }
  }
}
