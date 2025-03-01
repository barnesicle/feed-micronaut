FROM amazoncorretto:21

MAINTAINER  Luke Barnes
RUN java -version
ENV ARTIFACT_NAME=Feed-0.1-all.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build/libs/$ARTIFACT_NAME $APP_HOME

EXPOSE 8080

ENTRYPOINT exec java \
-Djava.security.egd=file:/dev/./urandom \
-jar $ARTIFACT_NAME
