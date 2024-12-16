package org.study.app.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.dto.CreateUserRequestDTO;

import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

  @POST
  public Response createUser(CreateUserRequestDTO request) {
    return Response.ok(request).build();
  }


  @GET
  public Response listAllUsers() {
    var users = List.of(new CreateUserRequestDTO("me", 21));
    return Response.ok(users).build();
  }
}
