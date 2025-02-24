package com.motivationadivisor.feed.messaging;

import com.motivationadivisor.feed.messaging.sqs.SQSQueueCreator;
import com.motivationadivisor.feed.messaging.sqs.SQSQueueDetails;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerShutdownEvent;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

@Singleton
public class SqsClientCreatedEventListener implements BeanCreatedEventListener<SqsClient> {

    @Value("${sqs.queue.newEvent.name}")
    String feedQueueName;

    @Value("${sqs.queue.notifyUsers.name}")
    String notifyUsersQueueName;

    @Override
    public SqsClient onCreated(BeanCreatedEvent<SqsClient> event) {
        SqsClient client = event.getBean();

        System.out.println("onCreated: Creating queue for " + feedQueueName);

        var queues = client.listQueues().queueUrls();

        if (queues.stream().noneMatch(it -> it.contains(feedQueueName))) {
            client.createQueue(
                    CreateQueueRequest.builder()
                            .queueName(feedQueueName)
                            .build()
            );

        }

        SQSQueueDetails details = SQSQueueCreator.createQueue(client, notifyUsersQueueName);
        System.out.println("onCreated: Creating queue for " + details.arn());


        return client;
    }

}