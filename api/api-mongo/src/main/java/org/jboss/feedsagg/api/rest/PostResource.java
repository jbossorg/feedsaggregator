package org.jboss.feedsagg.api.rest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jboss.feedsagg.api.BlogPostMongoService;
import org.jboss.feedsagg.api.model.BlogPost;

import io.smallrye.mutiny.Uni;

@Path("/rest/v1/post")
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    BlogPostMongoService blogPostMongoService;

    @GET
    @Path("{id}")
    public Uni<BlogPost> getPost(@PathParam("id") String id) {
        return blogPostMongoService.getPostById(id).onFailure(IllegalArgumentException.class).transform(failure -> new BadRequestException(failure.getMessage()));
    }

}
