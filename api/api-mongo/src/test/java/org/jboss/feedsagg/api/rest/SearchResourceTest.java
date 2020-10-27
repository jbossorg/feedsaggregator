package org.jboss.feedsagg.api.rest;

import static io.restassured.RestAssured.given;

import org.jboss.feedsagg.api.model.BlogPost;
import org.jboss.feedsagg.api.testutils.MongoTestResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link SearchResource}
 */
@QuarkusTest
@QuarkusTestResource(MongoTestResource.class)
class SearchResourceTest {

    @Test
    void testSearch() {
        BlogPost[] posts = given().when().get("/rest/v1/search").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(MongoTestResource.getImportedDocuments().size(), posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0]);
    }

    @Test
    void testSearch_tags() {
        BlogPost[] posts = given().when().get("/rest/v1/search?tag=t1&tag=t2").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(2, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0]);
    }

}