package org.jboss.feedsagg.api.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import org.jboss.feedsagg.api.testutils.MongoTestResource;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link io.quarkus.mongodb.health.MongoHealthCheck}
 */
@QuarkusTest
@QuarkusTestResource(MongoTestResource.class)
class ReactiveMongoHealthCheckTest {

    @Test
    void testHealthCheck() {
        given().when().get("/health/ready").then().statusCode(200).body("status", equalTo("UP")).body("checks.status", containsInAnyOrder("UP")).body("checks.name", containsInAnyOrder("MongoDB connection health check"));
    }

}