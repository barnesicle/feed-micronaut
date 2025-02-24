package com.motivationadivisor.feed.messaging.sns;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

public class SNSQueueSubscriber {
    public static String subscribeQueue(SnsClient snsClient, String topicArn, String queueArn) {
        SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("sqs")
                .endpoint(queueArn)
                .build();
        SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
        return subscribeResponse.subscriptionArn();
    }
}
