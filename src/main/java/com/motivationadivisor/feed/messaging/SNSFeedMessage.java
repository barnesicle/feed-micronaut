package com.motivationadivisor.feed.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SNSFeedMessage {

    @JsonProperty("Message")
    private String message;

    public SNSFeedMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SNSFeedMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
