/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.8/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("io.freefair.lombok") version "8.13"
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit for testing.
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")

    implementation(libs.guava)

    // Lambda
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.1")

    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // AWS SSM
    implementation("software.amazon.awssdk:ssm:2.24.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "com.fryrank.handler.ReviewHandler"
}

tasks {
    test {
        useJUnit()  // Use JUnit 4
        
        // Enable test output in console
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            showExceptions = true
            showCauses = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            // Add more detailed output
            info {
                events("passed", "skipped", "failed", "standardOut", "standardError")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }
        
        // Generate HTML test report
        reports {
            html.required.set(true)
            junitXml.required.set(true)
        }
    }

    val buildZip by creating(Zip::class) {
        from(compileJava)
        from(processResources)
        into("lib") {
            from(configurations.runtimeClasspath)
        }
    }

    build {
        dependsOn(test)
        dependsOn(buildZip)
    }
}
