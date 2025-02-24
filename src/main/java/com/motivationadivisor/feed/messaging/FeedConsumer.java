package com.motivationadivisor.feed.messaging;

import com.motivationadivisor.feed.data.FakeData;
import com.motivationadivisor.feed.Feed;
import com.motivationadivisor.feed.sockets.FeedSocketSessionValidator;
import com.motivationadivisor.feed.domain.FeedDAO;
import com.motivationadivisor.feed.repositories.FeedRepository;
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.websocket.WebSocketBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.micronaut.jms.sqs.configuration.SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME;


@JMSListener( value=CONNECTION_FACTORY_BEAN_NAME, errorHandlers= DemoJMSListenerErrorHandler2.class)
public class FeedConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(FeedConsumer.class);

    private final FeedRepository feedRepository;
    private final WebSocketBroadcaster broadcaster;

    private final FeedSocketSessionValidator feedSocketSessionValidator = new FeedSocketSessionValidator();
    private final ObjectMapper objectMapper;

    public FeedConsumer(FeedRepository feedRepository, final WebSocketBroadcaster broadcaster, ObjectMapper objectMapper) {
        this.feedRepository = feedRepository;
        this.broadcaster = broadcaster;
        this.objectMapper = objectMapper;
    }

    /*@Queue(value = "demo_queue", acknowledgeMode = Session.CLIENT_ACKNOWLEDGE)
    public void receive(@Message jakarta.jms.Message message) throws Exception {

        String body = ((TextMessage) message).getText();

        LOG.info("Message has been consumed. Message body: {}", body);
        if (body.equals("fail")) {
            throw new Exception("woops");
        } else {
            message.acknowledge();
        }
    }*/


    @Queue(value ="${sqs.queue.notifyUsers.name}")
    public void receiveNotifyUsersEvent(@MessageBody SNSFeedMessage message) throws Exception {
        final MessageBodyData messageBodyData = objectMapper.readValue(message.getMessage(), MessageBodyData.class);

        final String body = messageBodyData.message();
        final String userId = messageBodyData.userId();
        final String type = messageBodyData.type();

        LOG.info("Notifying socket connections. Message body: {},  userId {}  and type {}", body, userId, type);

        FakeData.getUserFriendsForUser(userId).forEach(friend -> {
            broadcaster.broadcastAsync(new Feed(type, body), feedSocketSessionValidator.isValid(friend));
        });

    }


    @Queue(value ="${sqs.queue.newEvent.name}")
    public void receiveFeedEvent(@MessageBody SNSFeedMessage message) throws Exception {
        final MessageBodyData messageBodyData = objectMapper.readValue(message.getMessage(), MessageBodyData.class);

        final String body = messageBodyData.message();
        final String userId = messageBodyData.userId();
        final String type = messageBodyData.type();

        LOG.info("Message has been consumed. Message body: {},  userId {}  and type {}", body, userId, type);

        final FeedDAO feed = new FeedDAO(0L, body, type, userId);
        feedRepository.save(feed);

    }

}