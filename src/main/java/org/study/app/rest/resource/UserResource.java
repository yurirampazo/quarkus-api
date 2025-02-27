package org.study.app.rest.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreateUserRequestDTO;

import java.util.Objects;
import java.util.Optional;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository userRepository;

    @Inject
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequestDTO requestDTO) {

        var user = new User();
        user.setAge(requestDTO.getAge());
        user.setName(requestDTO.getName());

        userRepository.persist(user);
//        PANACHE Samples methods
//        user.delete();
//        User.count();
//        User.delete("DELETE FROM ...")

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        return Response.ok(userRepository.findAll().list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        var response = (User) userRepository.findById(id);

        if (Objects.nonNull(response)) {
            userRepository.delete(response);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Integer id, CreateUserRequestDTO requestDTO) {

        User user = userRepository.findById(Long.valueOf(id));

        if (user != null) {
            user.setName(isNullOrEmpty(requestDTO.getName()) ? user.getName() : requestDTO.getName());
            user.setAge(requestDTO.getAge() != null ? requestDTO.getAge() : user.getAge());
//            userRepository.update() -> NOt necessary, transactional commits the instance changes in the database
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    public static boolean isNullOrEmpty(String text) {
        return text != null && !text.isBlank();
    }
}
