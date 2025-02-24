package com.motivationadivisor.feed;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Feed {
    private String messageId;
    private String message;

    public Feed() {
    }

    public Feed(String messageId, String message) {
        this.messageId = messageId;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
