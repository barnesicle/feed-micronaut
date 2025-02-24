plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.5"
    id("io.micronaut.test-resources") version "4.4.5"
    //id("org.graalvm.buildtools.native") version "0.10.5"
}

version = "0.1"
group = "com.motivationadivisor.feed"

repositories {
    mavenCentral()

}


dependencies {
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut:micronaut-graal")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")

    // https://mvnrepository.com/artifact/io.micronaut/micronaut-http-server-netty
    runtimeOnly("io.micronaut:micronaut-http-server-netty:4.8.3")

    implementation("io.micrometer:context-propagation")
    implementation("io.micronaut.kafka:micronaut-kafka")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    runtimeOnly("ch.qos.logback:logback-classic")
    // https://mvnrepository.com/artifact/software.amazon.awssdk/sns
    implementation("software.amazon.awssdk:sns")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")

    // https://mvnrepository.com/artifact/io.micronaut.jms/micronaut-jms-core
    implementation("io.micronaut.jms:micronaut-jms-core:4.2.0")

    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.jms:micronaut-jms-sqs")
    // https://mvnrepository.com/artifact/io.micronaut/micronaut-websocket
    implementation("io.micronaut:micronaut-websocket:4.8.3")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("io.micronaut.reactor:micronaut-reactor")
    compileOnly("io.micronaut:micronaut-http-client")

    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("org.postgresql:postgresql")
}


application {
    mainClass = "com.motivationadivisor.feed.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}

/*graalvmNative {
    binaries {
        main {
            imageName.set('myApp')
            buildArgs.add('-Ob')
        }
    }
}*/



micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.motivationadivisor.feed.*")
    }
    testResources {
        sharedServer = true
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}




