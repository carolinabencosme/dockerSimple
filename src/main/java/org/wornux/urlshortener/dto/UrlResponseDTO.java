package org.wornux.urlshortener.dto;

import java.util.List;

public record UrlResponseDTO(
    String originalUrl,
    String shortenedUrl,
    String createdBy,
    String createdAt,
    List<AnalyticsDTO> analytics) {}
