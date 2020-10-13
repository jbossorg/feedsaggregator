package org.jboss.feedsagg.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link PostResource}
 */
@QuarkusTest
class PostResourceTest {

    @Test
    void testGetPost() {
        given().when().get("/rest/v1/post/test-id").then().statusCode(200).body("id", equalTo("test-id")).body("code", equalTo("test-code"));
    }

}