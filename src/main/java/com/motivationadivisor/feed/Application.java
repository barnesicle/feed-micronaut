package com.motivationadivisor.feed;

import io.micronaut.runtime.Micronaut;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}