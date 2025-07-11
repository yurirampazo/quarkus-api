package org.study.app.rest.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.domain.model.Follower;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.FollowerRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreateUserRequestDTO;
import org.study.app.rest.dto.FollowerRequestDTO;
import org.study.app.rest.dto.ResponseError;

import java.util.Objects;
import java.util.Set;

import static org.study.app.rest.dto.ResponseError.UNPROCESSABLE_ENTITY;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

  private final UserRepository userRepository;
  private final FollowerRepository followerRepository;
  private final Validator validator;
  private final ObjectMapper mapper;

  @Inject
  public FollowerResource(UserRepository userRepository, FollowerRepository followerRepository,Validator validator) {
    this.userRepository = userRepository;
    this.followerRepository = followerRepository;
    this.validator = validator;
    mapper = new ObjectMapper();
  }

  @PUT
  public Response followUser(@PathParam("userId") Long userId, FollowerRequestDTO request) {
    var user = userRepository.findById(userId);
    if(user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    var follower = userRepository.findById(request.getFollowerId());
    if(follower == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    var entity = new Follower();
    entity.setUser(user);
    entity.setFollower(follower);

    followerRepository.persist(entity);

    return Response.status(Response.Status.NO_CONTENT).build();



  }
}
