package org.wornux.urlshortener.dto;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

public record UrlStatsDTO(
    ObjectId id,
    String originalUrl,
    String shortenedUrl,
    Date createdAt,
    int clickCount,
    int totalAccesses,
    int uniqueVisitors,
    Date lastAccess,
    List<String> userAgents,
    List<String> operatingSystems) {}
