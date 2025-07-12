package org.study.app.rest.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.study.app.domain.model.Follower;
import org.study.app.domain.repository.FollowerRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.FollowerRequestDTO;

import java.util.HashMap;

@Slf4j
@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

  private final UserRepository userRepository;
  private final FollowerRepository followerRepository;
  private final Validator validator;
  private final ObjectMapper mapper;

  @Inject
  public FollowerResource(UserRepository userRepository, FollowerRepository followerRepository, Validator validator) {
    this.userRepository = userRepository;
    this.followerRepository = followerRepository;
    this.validator = validator;
    mapper = new ObjectMapper();
  }


  @PUT
  @Transactional
  public Response followUser(@PathParam("userId") Long userId, FollowerRequestDTO request) {

    if (userId.equals(request.getFollowerId())) {
      var message = "You can´t follow yourself";
      var responseMap = new HashMap<>();
      responseMap.put("responseMessage", message);
      try {
        return Response.status(409).entity(mapper.writeValueAsString(responseMap)).build();
      } catch (JsonProcessingException e) {
        return Response.status(409).entity(message).build();
      }
    }

    var user = userRepository.findById(userId);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    var follower = userRepository.findById(request.getFollowerId());
    if (follower == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    var follows = followerRepository.follows(follower, user);

    if (!follows) {
      log.info("Follower doesnt follow user yet!");
      var entity = new Follower();
      entity.setUser(user);
      entity.setFollower(follower);
      followerRepository.persist(entity);
    } else {
      log.info("User {} already followed by follower {}", userId, follower.getId());
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
