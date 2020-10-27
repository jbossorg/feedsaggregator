package org.jboss.feedsagg.api.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link PostResource}
 */
@QuarkusTest
class PostResourceTest {

    @Test
    void testHealthCheckDown() {
        // Check correct readiness health check
        given().when().get("/health/ready").then()
                .statusCode(503)
                .body("status", equalTo("DOWN"))
                .body("checks.status", containsInAnyOrder("DOWN"))
                .body("checks.name", containsInAnyOrder("MongoDB connection health check"));
    }

    @Test
    void testGetPost() {
        given().when().get("/rest/v1/post/my_test_code").then()
                .statusCode(200)
                .body("id", equalTo("test-id"))
                .body("code", equalTo("my_test_code"));
    }
    
    @Test
    void testGetPost_unknown() {
        given().when().get("/rest/v1/unknown").then()
                .statusCode(404);
    }

}