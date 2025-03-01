package com.lbarnes.feed.messaging.sns;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

import java.util.HashMap;
import java.util.Map;

public class SNSQueuePolicy {

    public static void setQueuePolicy(SqsClient sqsClient, String queueUrl, String topicArn) {
        GetQueueAttributesRequest getQueueAttributesRequest = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .build();
        GetQueueAttributesResponse getQueueAttributesResponse = sqsClient.getQueueAttributes(getQueueAttributesRequest);
        String queueArn = getQueueAttributesResponse.attributes().get(QueueAttributeName.QUEUE_ARN);

        String policy = "{" + // TODO Move to file.
                "  \"Version\": \"2012-10-17\"," +
                "  \"Id\": \"" + queueArn + "/SQSQueuePolicy\"," +
                "  \"Statement\": [" +
                "    {" +
                "      \"Sid\": \"Allow-SNS-SendMessage\"," +
                "      \"Effect\": \"Allow\"," +
                "      \"Principal\": {" +
                "        \"Service\": \"sns.amazonaws.com\"" +
                "      }," +
                "      \"Action\": \"SQS:SendMessage\"," +
                "      \"Resource\": \"" + queueArn + "\"," +
                "      \"Condition\": {" +
                "        \"ArnEquals\": {" +
                "          \"aws:SourceArn\": \"" + topicArn + "\"" +
                "        }" +
                "      }" +
                "    }" +
                "  ]" +
                "}";

        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.POLICY, policy);

        SetQueueAttributesRequest setQueueAttributesRequest = SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributes(attributes)
                .build();

        sqsClient.setQueueAttributes(setQueueAttributesRequest);
        System.out.println("SQS queue policy set.");
    }

}
