package com.lbarnes.feed.rest;

import com.lbarnes.feed.messaging.DemoProducer;
import com.lbarnes.feed.messaging.Feed;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

@Controller("/feeds")
public class FeedController {

    private final DemoProducer demoProducer;

    public FeedController(DemoProducer demoProducer) {
        this.demoProducer = demoProducer;
    }

    // NOTE: Does not make sense to have this here but it is just here for convenience.
    @Post
    public Feed createFeedItem(@Body Feed feed) {
        demoProducer.send(feed.getMessage());
        return new Feed();
    }

    @Get
    public Feed getFeed() {

        return new Feed();
    }

}
