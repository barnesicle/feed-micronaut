package com.motivationadivisor.feed.messaging;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.jms.listener.JMSListenerErrorHandler;
import jakarta.inject.Singleton;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DemoJMSListenerErrorHandler2 implements JMSListenerErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DemoJMSListenerErrorHandler2.class);

    @Override
    public void handle(@NonNull Session session, @NonNull Message message, @NonNull Throwable ex) {
        LOG.info("!!! Message has been handled. Message body: {}", message);
        if (ex == null) {
            try {
                message.acknowledge();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
