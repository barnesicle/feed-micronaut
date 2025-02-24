package com.motivationadivisor.feed.messaging;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MessageBodyData(String message, String userId, String type) {
}
