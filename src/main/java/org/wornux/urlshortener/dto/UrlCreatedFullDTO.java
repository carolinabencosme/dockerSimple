package org.wornux.urlshortener.dto;

import java.util.Date;

public record UrlCreatedFullDTO(
    String originalUrl,
    String shortenedUrl,
    Date createdAt,
    UrlStatsDTO stats,
    String sitePreviewBase64) {}
