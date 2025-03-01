package com.lbarnes.feed.messaging.sns;

import com.lbarnes.feed.data.FakeData;
import com.lbarnes.feed.messaging.Feed;
import com.lbarnes.feed.messaging.MessageBodyData;
import com.lbarnes.feed.sockets.FeedSocketSessionValidator;
import com.lbarnes.feed.messaging.sqs.SQSQueueCreator;
import com.lbarnes.feed.messaging.sqs.SQSQueueDetails;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerShutdownEvent;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.io.IOException;

@Singleton
public class SnsSubscriber {

    private final SnsClient snsClient;
    private final SqsClient sqsClient;
    private final String topicArn;
    private final String notifyFeedName;
    private final String feedQueueName;

    private String subscriptionArn;
    private String sqsQueueUrl;


    private final WebSocketBroadcaster broadcaster;
    private final ObjectMapper objectMapper;

    private final FeedSocketSessionValidator feedSocketSessionValidator = new FeedSocketSessionValidator();

    @Inject
    public SnsSubscriber(SnsClient snsClient, SqsClient sqsClient,
                         @Value("${sns.topic.arn}") String topicArn,
                         @Value("${sqs.queue.notifyUsers.name}") String queueName,
                         @Value("${sqs.queue.newEvent.name}") String feedQueueName,
                         final WebSocketBroadcaster broadcaster,
                         ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.sqsClient = sqsClient;
        this.topicArn = topicArn;
        this.notifyFeedName = queueName;
        this.broadcaster = broadcaster;
        this.objectMapper = objectMapper;
        this.feedQueueName = feedQueueName;
    }

    private void setupFeedQueueAndSubscription() {
        SQSQueueDetails queueDetails = SQSQueueCreator.createQueue(sqsClient, feedQueueName);

        System.out.println("Queue ARN " + queueDetails.arn());

        SNSQueuePolicy.setQueuePolicy(sqsClient, queueDetails.url(), topicArn);

        // TODO May want to create this in a sam cli yaml file and create just once?
        String subscriptionArn = SNSQueueSubscriber.subscribeQueue(snsClient, topicArn, queueDetails.arn());

        System.out.println("Queue ARN " + subscriptionArn);
    }


    private void setupNotifyQueueAndSubscription() {
        SQSQueueDetails queueDetails = SQSQueueCreator.createQueue(sqsClient, notifyFeedName);
        System.out.println("Queue ARN " + queueDetails.arn());
        SNSQueuePolicy.setQueuePolicy(sqsClient, queueDetails.url(), topicArn);
        subscriptionArn = SNSQueueSubscriber.subscribeQueue(snsClient, topicArn, queueDetails.arn());
        System.out.println("Queue ARN " + subscriptionArn);

    }


    @EventListener
    public void onStartup(ServerStartupEvent event) {
        setupFeedQueueAndSubscription();
        setupNotifyQueueAndSubscription();
        //startPollingQueue();
    }

    private void setupQueueAndSubscription() {
        // Create SQS Queue
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(notifyFeedName)
                .build();
        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        sqsQueueUrl = createQueueResponse.queueUrl();

        GetQueueAttributesResponse queueAttributes = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .queueUrl(sqsQueueUrl)
                .build());

        String queueArn = queueAttributes.attributes().get(QueueAttributeName.QUEUE_ARN);

        new SNSQueuePolicy().setQueuePolicy(sqsClient, sqsQueueUrl, topicArn);

        System.out.println("Queue ARN " + queueArn);

        // Subscribe SQS Queue to SNS Topic
        SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("sqs")
                .endpoint(queueArn)
                .build();
        SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
        subscriptionArn = subscribeResponse.subscriptionArn();

        System.out.println("Subscribed SQS queue to SNS topic. Subscription ARN: " + subscribeResponse.subscriptionArn());
    }



    private void startPollingQueue() {

        System.out.println("Starting polling");

        new Thread(() -> {
            while (true) {

                System.out.println("Waiting for message....");

                ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                        .queueUrl(sqsQueueUrl)
                        .waitTimeSeconds(20) // Long polling
                        .maxNumberOfMessages(1)
                        .build();

                ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);

                for (Message message : receiveMessageResponse.messages()) {
                    System.out.println("Received message: " + message.body());


                    // Your message processing logic here
                    try {
                        processMessage(objectMapper.readValue(message.body(), SNSFeedMessage.class));
                        // Delete message after processing
                        sqsClient.deleteMessage(builder -> builder.queueUrl(sqsQueueUrl).receiptHandle(message.receiptHandle()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }).start();
    }

    private void processMessage(SNSFeedMessage message) throws IOException {
        // Your code to execute on every instance
        System.out.println("Processing message on instance: " + message);
        // Add your business logic here.
        final MessageBodyData messageBodyData = objectMapper.readValue(message.getMessage(), MessageBodyData.class);

        final String body = messageBodyData.message();
        final String userId = messageBodyData.userId();
        final String type = messageBodyData.type();

        FakeData.getUserFriendsForUser(userId).forEach(friend -> {
            broadcaster.broadcastAsync(new Feed(type, body, friend), feedSocketSessionValidator.isValid(friend));
        });

    }

    @EventListener
    public void onShutdown(ServerShutdownEvent event) {
        unsubscribeFromSns();
        deleteSQSQueue();
    }

    private void deleteSQSQueue() {
        try {
            DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder().queueUrl(notifyFeedName).build();
            sqsClient.deleteQueue(deleteQueueRequest);
        } catch (Exception e) {
            System.err.println("Error deleting SQS queue: " + e.getMessage());
        }
    }

    private void unsubscribeFromSns() {
        if (subscriptionArn != null) {
            try {
                UnsubscribeRequest unsubscribeRequest = UnsubscribeRequest.builder()
                        .subscriptionArn(subscriptionArn)
                        .build();
                snsClient.unsubscribe(unsubscribeRequest);
                System.out.println("Unsubscribed from SNS topic. Subscription ARN: " + subscriptionArn);
            } catch (Exception e) {
                System.err.println("Error unsubscribing from SNS topic: " + e.getMessage());
            }
        }
    }

}