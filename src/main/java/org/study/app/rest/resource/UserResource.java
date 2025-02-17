package org.study.app.rest.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.domain.model.User;
import org.study.app.rest.dto.CreateUserRequestDTO;

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
}
