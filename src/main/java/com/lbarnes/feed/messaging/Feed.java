package com.lbarnes.feed.messaging;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Feed {
    private String messageId;
    private String message;
    private String userId;

    public Feed() {
    }

    public Feed(String messageId, String message, String userId) {
        this.messageId = messageId;
        this.message = message;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
