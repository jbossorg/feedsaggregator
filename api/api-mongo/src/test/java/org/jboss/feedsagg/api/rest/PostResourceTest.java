package org.jboss.feedsagg.api.rest;

import static io.restassured.RestAssured.given;

import org.jboss.feedsagg.api.model.BlogPost;
import org.jboss.feedsagg.api.testutils.MongoTestResource;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test of {@link PostResource}
 */
@QuarkusTest
@QuarkusTestResource(MongoTestResource.class)
class PostResourceTest {

    @Test
    void testGetPost() {
        BlogPost post = given().when().get("/rest/v1/post/my_test_code").then().statusCode(200).extract().as(BlogPost.class);
        MongoTestResource.verifyResponseBlogPost("my_test_code", post);
    }

    @Test
    void testGetPost_unknown() {
        given().when().get("/rest/v1/post/unknown").then().statusCode(204);
    }

}