package com.motivationadivisor.feed.domain;


import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@MappedEntity( value = "feed")
public record FeedDAO(@Id @GeneratedValue Long id,
                   @NonNull @NotBlank @Size(max = 255) String message,
                   @NonNull @NotBlank @Size(max = 255) String type,
                   @Nullable String userId) {
}