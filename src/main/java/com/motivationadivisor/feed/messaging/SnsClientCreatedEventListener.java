/*
package com.motivationadivisor.feed.messaging;

import com.motivationadivisor.feed.messaging.sns.SNSQueueSubscriber;
import com.motivationadivisor.feed.messaging.sqs.SQSQueueCreator;
import com.motivationadivisor.feed.messaging.sqs.SQSQueueDetails;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Singleton
public class SnsClientCreatedEventListener implements BeanCreatedEventListener<SnsClient> {

    @Value("${sqs.queue.notifyUsers.name}")
    String notifyUsersQueueName;

    @Value("${sns.topic.arn}")
    String topicArn;

    String subscriptionArn;

    @Inject
    private SqsClient sqsClient;


    public SnsClientCreatedEventListener(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    // TODO unique queue name for instance. Delete?

    @Override
    public SnsClient onCreated(BeanCreatedEvent<SnsClient> event) {
        SnsClient snsClient = event.getBean();

        System.out.println("onCreated: Creating SNS queue");

        SQSQueueDetails queueDetails = SQSQueueCreator.createQueue(sqsClient, notifyUsersQueueName);
        System.out.println("Queue ARN " + queueDetails.arn());
        SNSQueuePolicy.setQueuePolicy(sqsClient, queueDetails.url(), topicArn);
        subscriptionArn = SNSQueueSubscriber.subscribeQueue(snsClient, topicArn, queueDetails.arn());

        return snsClient;
    }

}*/
