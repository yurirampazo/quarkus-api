package org.study.app.rest.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.AppUtils;
import org.study.app.domain.model.Post;
import org.study.app.domain.repository.FollowerRepository;
import org.study.app.domain.repository.PostRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreatePostRequestDTO;
import org.study.app.rest.dto.PostResponseDTO;

import java.util.HashMap;
import java.util.Map;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

  private final PostRepository postRepository;
  private final FollowerRepository followerRepository;
  private final AppUtils appUtils;
  private final Validator validator;
  private final ObjectMapper mapper;

  @Inject
  public PostResource(FollowerRepository followerRepository, PostRepository postRepository, Validator validator, AppUtils appUtils) {
    this.postRepository = postRepository;
    this.followerRepository = followerRepository;
    this.appUtils = appUtils;
    this.validator = validator;
    mapper = new ObjectMapper();
  }

  @POST
  @Transactional
  public Response savePost(@PathParam("userId") Long userId, CreatePostRequestDTO requestDTO) {
    var optionalUser = appUtils.findUserIfExist(userId);
    if (optionalUser.isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    Post post = new Post();
    post.setText(requestDTO.getText());
    post.setUser(optionalUser.get());
    postRepository.persist(post);

    return Response.status(Response.Status.CREATED.getStatusCode()).build();
  }

  @GET
  public Response listPosts(@PathParam("userId") Long userId,
                            @HeaderParam("followerId") Long followerId) {
    var optionalUser = appUtils.findUserIfExist(userId);
    final var RESPONSE_MESSAGE = "responseMessage";
    var mapResponse = new HashMap<String, Object>();
    if (optionalUser.isEmpty()) {
      mapResponse.put(RESPONSE_MESSAGE, "User not found!");
      return Response.status(Response.Status.NOT_FOUND).entity(jsonResponseFromMap(mapResponse)).build();
    }
    var user = optionalUser.get();


    var optionalFollower = appUtils.findUserIfExist(followerId);
    if (optionalFollower.isEmpty()) {
      mapResponse.put(RESPONSE_MESSAGE, "No user found for follower id");
      return Response.status(Response.Status.NOT_FOUND).entity(jsonResponseFromMap(mapResponse)).build();
    }
    var follower = optionalFollower.get();
    var follows = followerRepository.follows(follower, user);
    if (!follows) {
      mapResponse.put(RESPONSE_MESSAGE, "You must follow the user to see it's posts");
      return Response.status(Response.Status.FORBIDDEN).entity(jsonResponseFromMap(mapResponse)).build();
    }

    /*
     * List posts by user
     * */
    var query = postRepository.find("user",
          Sort.by("dateTime", Sort.Direction.Descending),
          user);
    var list = query.list();
    var postResponses = list.stream()
          .map(PostResponseDTO::mapFromPost)
          .toList();
    return Response.ok(postResponses).build();
  }


  private <T> String jsonResponseFromMap(Map<String, T> mapResponse) {
    try {
      return mapper.writeValueAsString(mapResponse);
    } catch (JsonProcessingException e) {
      return mapResponse.toString();
    }
  }
}
