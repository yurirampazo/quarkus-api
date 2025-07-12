package org.study.app.rest.resource;

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
import org.study.app.domain.repository.PostRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreatePostRequestDTO;
import org.study.app.rest.dto.PostResponseDTO;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final AppUtils appUtils;
  private final Validator validator;
  private final ObjectMapper mapper;

  @Inject
  public PostResource(UserRepository userRepository, PostRepository postRepository, Validator validator, AppUtils appUtils) {
    this.userRepository = userRepository;
    this.postRepository = postRepository;
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
  public Response listPosts(@PathParam("userId") Long userId) {
    var optionalUser = appUtils.findUserIfExist(userId);
    if (optionalUser.isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    /*
     * List posts by user
     * */
    var query = postRepository.find("user",
          Sort.by("dateTime", Sort.Direction.Descending),
          optionalUser.get());
    var list = query.list();
    var postResponses = list.stream()
          .map(PostResponseDTO::mapFromPost)
          .toList();
    return Response.ok(postResponses).build();
  }
}
