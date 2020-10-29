package org.jboss.feedsagg.api.rest;

import static io.restassured.RestAssured.given;

import org.jboss.feedsagg.api.testutils.MongoTestResource;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of the OpenApi doc availability
 */
@QuarkusTest
@QuarkusTestResource(MongoTestResource.class)
class OpenApiDocAvailableTest {

    @Test
    void testGetPost() {
        given().when().get("/openapi").then().statusCode(200).log().body();
    }

}