package org.jboss.feedsagg.api.rest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.feedsagg.api.BlogPostMongoService;
import org.jboss.feedsagg.api.model.BlogPost;

import io.smallrye.mutiny.Uni;

@Path("/rest/v1/post")
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    BlogPostMongoService blogPostMongoService;

    @GET
    @Path("{code}")
    @Operation(description = "Get one blogpost by it's code")
    public Uni<BlogPost> getPost(@PathParam("code") String code) {
        return blogPostMongoService.getPostByCode(code);
    }

}
