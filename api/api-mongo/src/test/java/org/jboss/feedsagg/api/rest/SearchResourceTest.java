package org.jboss.feedsagg.api.rest;

import static io.restassured.RestAssured.given;

import org.jboss.feedsagg.api.model.BlogPost;
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
        // id is not published over the API!
        Assertions.assertNull(posts[0].getId());
        Assertions.assertEquals("Conditions:", posts[0].getContent());
    }

    @Test
    void testSearch_tags() {
        BlogPost[] posts = given().when().get("/rest/v1/search?tag=t1&tag=t2").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(1, posts.length);
        // id is not published over the API!
        Assertions.assertNull(posts[0].getId());
        Assertions.assertEquals("Conditions: tags: [t1, t2]", posts[0].getContent());
    }
}