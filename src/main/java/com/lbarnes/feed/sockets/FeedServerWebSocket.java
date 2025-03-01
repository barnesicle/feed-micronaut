package com.lbarnes.feed.sockets;

import com.lbarnes.feed.data.FakeData;
import com.lbarnes.feed.messaging.Feed;
import com.lbarnes.feed.repositories.FeedRepository;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

@ServerWebSocket("/ws/feed/{username}")
public class FeedServerWebSocket {

    private static final Logger LOG = LoggerFactory.getLogger(FeedServerWebSocket.class);

    private final WebSocketBroadcaster broadcaster;
    private final FeedRepository feedRepository;
    private final FeedSocketSessionValidator feedSocketSessionValidator = new FeedSocketSessionValidator();

    public FeedServerWebSocket(WebSocketBroadcaster broadcaster, FeedRepository feedRepository) {
        this.broadcaster = broadcaster;
        this.feedRepository = feedRepository;
    }

    @OnOpen
    public Publisher<List<Feed>> onOpen(String username, WebSocketSession session) {
        log("onOpen", session, username);
        // TODO Should find all for friends.

        var friends = FakeData.getUserFriendsForUser(username);

        var feedsToBroadcastTo = friends.stream().map(feedRepository::findAllByUserId).flatMap(Collection::stream).distinct().toList();

        return broadcaster.broadcast(feedsToBroadcastTo.stream().map( feedDAO -> new Feed(feedDAO.messageId(), feedDAO.message(), feedDAO.userId()) ).toList(), feedSocketSessionValidator.isValid(username));
    }

    @OnMessage
    public Publisher<String> onMessage(
            String username,
            String message,
            WebSocketSession session) {

        log("onMessage", session, username);
        return broadcaster.broadcast(String.format("[%s] %s", username, message), feedSocketSessionValidator.isValid(username));
    }

    @OnClose
    public Publisher<String> onClose(
            String username,
            WebSocketSession session) {

        log("onClose", session, username);
        return broadcaster.broadcast(String.format("[%s] Leaving!", username), feedSocketSessionValidator.isValid(username));
    }

    private void log(String event, WebSocketSession session, String username) {
        LOG.info("* WebSocket: {} received for session {} from '{}'",
                event, session.getId(), username);
    }


}