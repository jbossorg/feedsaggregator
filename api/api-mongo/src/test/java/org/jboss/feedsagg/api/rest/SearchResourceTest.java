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

        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0], false);
    }

    @Test
    void testSearch_sortAsc_content() {
        BlogPost[] posts = given().when().get("/rest/v1/search?sort=asc&content=true").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(MongoTestResource.getImportedDocuments().size(), posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-5", posts[0], true);
    }

    @Test
    void testSearch_sortUnknown() {
        BlogPost[] posts = given().when().get("/rest/v1/search?sort=sadas").then().statusCode(200).extract().as(BlogPost[].class);
        // it is ignored and default sorting is used
        Assertions.assertEquals(MongoTestResource.getImportedDocuments().size(), posts.length);
        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0], false);
    }

    @Test
    void testSearch_size() {
        BlogPost[] posts = given().when().get("/rest/v1/search?size=3").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(3, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0], false);
    }

    @Test
    void testSearch_sizeIsNegative() {
        BlogPost[] posts = given().when().get("/rest/v1/search?size=-3").then().statusCode(200).extract().as(BlogPost[].class);
        // default size is used in this case, so we get all docs we have in DB
        Assertions.assertEquals(MongoTestResource.getImportedDocuments().size(), posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0], false);
    }

    @Test
    void testSearch_sizeIsNaN() {
        // yep, really, Quarkus JAX-RS returns 404 in this case :-D
        given().when().get("/rest/v1/search?size=a").then().log().body().statusCode(404);
    }

    @Test
    void testSearch_size_from() {
        BlogPost[] posts = given().when().get("/rest/v1/search?size=3&from=2").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(3, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-3", posts[0], false);
    }

    @Test
    void testSearch_fromIsNegative() {
        BlogPost[] posts = given().when().get("/rest/v1/search?from=-2").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(MongoTestResource.getImportedDocuments().size(), posts.length);
        // 0 is used instead so we see first blogpost as first item in the response
        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0], false);
    }

    @Test
    void testSearch_fromIsNaN() {
        // yep, really, Quarkus JAX-RS returns 404 in this case :-D
        given().when().get("/rest/v1/search?from=a").then().log().body().statusCode(404);
    }

    @Test
    void testSearch_feed() {
        // empty params added to test they are ignored
        BlogPost[] posts = given().when().get("/rest/v1/search?feed=feed-other&feed=feed-2&feed=&feed= ").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(2, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-2", posts[0], false);
    }

    @Test
    void testSearch_feedExclude() {
        // empty params added to test they are ignored
        BlogPost[] posts = given().when().get("/rest/v1/search?feed_exclude=feed-other&feed_exclude=feed-1&feed_exclude=&feed_exclude= ").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(2, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-2", posts[0], false);
    }

    @Test
    void testSearch_group() {
        // empty params added to test they are ignored
        BlogPost[] posts = given().when().get("/rest/v1/search?group=gr-other&group=group-2&group=&group= ").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(3, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-2", posts[0], false);
    }

    @Test
    void testSearch_groupExclude() {
        // empty params added to test they are ignored
        BlogPost[] posts = given().when().get("/rest/v1/search?group_exclude=gr-other&group_exclude=group-1&group_exclude=&group_exclude= ").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(3, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-2", posts[0], false);
    }

    @Test
    void testSearch_tags() {
        // empty params added to test they are ignored
        BlogPost[] posts = given().when().get("/rest/v1/search?tag=t1&tag=t2&tag=&tag= ").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(2, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code", posts[0], false);
    }

    @Test
    void testSearch_tagsExclude() {
        // empty params added to test they are ignored
        BlogPost[] posts = given().when().get("/rest/v1/search?tag_exclude=t1&tag_exclude=t2&tag_exclude=&tag_exclude= ").then().statusCode(200).extract().as(BlogPost[].class);
        Assertions.assertEquals(3, posts.length);

        MongoTestResource.verifyResponseBlogPost("my_test_code-3", posts[0], false);
    }

}