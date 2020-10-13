package org.jboss.feedsagg.rest;

import static io.restassured.RestAssured.given;

import org.jboss.feedsagg.rest.model.BlogPost;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link SearchResource}
 */
@QuarkusTest
class SearchResourceTest {

    @Test
    void testSearch() {
        BlogPost[] posts = given().when().get("/rest/v1/search").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(1, posts.length);
        Assertions.assertEquals("test-id1", posts[0].getId());
    }

}