package org.study.app.rest.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.AppUtils;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreateUserRequestDTO;
import org.study.app.rest.dto.ResponseError;

import java.util.Optional;
import java.util.Set;

import static org.study.app.AppUtils.isNullOrEmpty;
import static org.study.app.rest.dto.ResponseError.UNPROCESSABLE_ENTITY;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

  private final UserRepository userRepository;
  private final AppUtils appUtils;
  private final Validator validator;

  @Inject
  public UserResource(UserRepository userRepository, Validator validator, AppUtils appUtils) {
    this.userRepository = userRepository;
    this.appUtils = appUtils;
    this.validator = validator;
  }

  @POST
  @Transactional
  public Response createUser(CreateUserRequestDTO requestDTO) {

    Set<ConstraintViolation<CreateUserRequestDTO>> validations = validator.validate(requestDTO);
    if (!validations.isEmpty()) {
      return ResponseError.createFromValidation(validations).withStatusCode(UNPROCESSABLE_ENTITY);
    }
    var user = new User();
    user.setAge(requestDTO.getAge());
    user.setName(requestDTO.getName());
    userRepository.persist(user);
    return Response.status(Response.Status.CREATED.getStatusCode())
          .entity(user)
          .build();
  }

  @GET
  public Response listAllUsers() {
    return Response.ok(userRepository.findAll().list()).build();
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response deleteUser(@PathParam("id") Long id) {
    Optional<User> userIfExist = appUtils.findUserIfExist(id);

    if (userIfExist.isPresent()) {
      userRepository.delete(userIfExist.get());
      return Response.noContent().build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Response updateUser(@PathParam("id") Long id, CreateUserRequestDTO requestDTO) {

    var opitonalUser = appUtils.findUserIfExist(id);

    if (opitonalUser.isPresent()) {
      var user = opitonalUser.get();
      user.setName(isNullOrEmpty(requestDTO.getName()) ? user.getName() : requestDTO.getName());
      user.setAge(requestDTO.getAge() != null ? requestDTO.getAge() : user.getAge());
//            userRepository.update() -> NOt necessary, transactional commits the instance changes in the database
      return Response.noContent().build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }
}
