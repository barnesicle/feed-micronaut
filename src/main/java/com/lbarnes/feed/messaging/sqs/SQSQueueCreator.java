package com.lbarnes.feed.messaging.sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class SQSQueueCreator {

    public static SQSQueueDetails createQueue(SqsClient sqsClient, String queueName) {
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();
        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        String queueUrl = createQueueResponse.queueUrl();

        GetQueueAttributesResponse queueAttributes = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .queueUrl(queueUrl)
                .build());

        return new SQSQueueDetails(queueUrl, queueAttributes.attributes().get(QueueAttributeName.QUEUE_ARN));
    }

}
