package org.study.app.rest.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.domain.model.User;
import org.study.app.rest.dto.CreateUserRequestDTO;

import java.util.Objects;
import java.util.Optional;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    @Transactional
    public Response createUser(CreateUserRequestDTO requestDTO) {

        var user = new User();
        user.setAge(requestDTO.getAge());
        user.setName(requestDTO.getName());

        user.persist();
//        PANACHE Samples methods
//        user.delete();
//        User.count();
//        User.delete("DELETE FROM ...")

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        return Response.ok(User.findAll().list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        var response = (User) User.findById(id);

        if (Objects.nonNull(response)) {
            response.delete();
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Integer id, CreateUserRequestDTO requestDTO) {

        User user = User.findById(id);

        if (user != null) {
            user.setName(isNullOrEmpty(requestDTO.getName()) ? user.getName() : requestDTO.getName());
            user.setAge(requestDTO.getAge() != null ? requestDTO.getAge() : user.getAge());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    public static boolean isNullOrEmpty(String text) {
        return text != null && !text.isBlank();
    }
}
