package com.lbarnes.feed.sockets;

import io.micronaut.websocket.WebSocketSession;

import java.util.function.Predicate;

public class FeedSocketSessionValidator {
    public Predicate<WebSocketSession> isValid(String username) {
        return s -> username.equalsIgnoreCase(s.getUriVariables().get("username", String.class, null));
    }
}
