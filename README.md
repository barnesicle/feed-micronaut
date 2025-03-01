
# Pending questions:
1. What happens if an instance goes down? The UI may handle this automatically, through postman the connection is removed.
2. What happens if you put a load balancer in front of WebSocket? Will NGINX work?

# Build

Build the jar. 

```
./gradlew assemble
```

# Starting

## AWS Setup

Create a SNS topic called "demo_queue". You can override this name by passing in an env variable called "SQS_QUEUE_NEW_EVENT_NAME". The SQS topics will be created for you.

Create credentials in IAM with the appropriate permissions. Make sure they have full access to SNS and SQS as the service will automatically create and remove SQS services and subscribe to the SNS.

## Running Postgres
```
docker run --network="host" --name postgres -e POSTGRES_PASSWORD=password -p 5432:5432 postgres
```

## 

## Building docker image
```
docker build -t feed .
```
## Run docker image

You will need to generate AWS credentials to pass in the docker container if running outside of AWS. 

AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY

```
export SQS_QUEUE_NEW_EVENT_NAME=...
export SNS_TOPIC_ARN=...
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_REGION=us-east-1
docker run --network="host" -e AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} -e AWS_REGION=${AWS_REGION} -t feed -p 8080:8080 
```

## Sending a message with the aws cli

```
aws sns publish --topic-arn arn:aws:sns:us-east-1:668548423029:demo_queue --message "{ \"message\" : \"Did something cool\", \"type\": \"Something\", \"userId\" : \"lbarnes\" }"
```




