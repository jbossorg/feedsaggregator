package org.jboss.feedsagg.api.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.feedsagg.api.BlogPostMongoService;
import org.jboss.feedsagg.api.model.BlogPost;

import io.smallrye.mutiny.Uni;

@Path("/rest/v1/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    @Inject
    BlogPostMongoService blogPostMongoService;

    @GET
    @Operation(description = "List blogposts with filtering and paging options.")
    public Uni<List<BlogPost>> search(
            @QueryParam("from") @Parameter(description = "Index of first returned item (for result-set paging, zero based)") Integer from, 
            @QueryParam("size") @Parameter(description = "Max number of returned items in the response, maximum is 100 (use result-set paging for more), (default 10)") Integer size, 
            @QueryParam("sort") @Parameter(description = "[asc|desc] Sorting oder of the result-set, they are always sorted by publishdate (default desc)") String sort, 
            @QueryParam("content") @Parameter(description = "Controls if 'content' field is returned in the response, as it is a really huge field in most cases, so this can save lots of network bandwidth. 'contentPreview' is always available, so ask for full content only if you really need it (default false)") boolean content,
            @QueryParam("feed") @Parameter(description = "Filter posts only for given feed, can be used multiple times to mix more feeds") List<String> feeds, 
            @QueryParam("feed_exclude") @Parameter(description = "Filter posts NOT to contain given feed, can be used multiple times to exclude more feeds") List<String> feedsExclude,
            @QueryParam("group") @Parameter(description = "Filter posts only for given group, can be used multiple times to mix more groups") List<String> groups,
            @QueryParam("group_exclude") @Parameter(description = "Filter posts NOT to contain given group, can be used multiple times to exclude more groups") List<String> groupsExclude,
            @QueryParam("tag") @Parameter(description = "Filter posts only for given tag, can be used multiple times to mix more tags") List<String> tags,
            @QueryParam("tag_exclude") @Parameter(description = "Filter posts NOT to contain given tag, can be used multiple times to exclude more tags") List<String> tagsExclude) {
        return blogPostMongoService.search(from, size, sort, content, feeds, feedsExclude, groups, groupsExclude, tags, tagsExclude);
    }

}
