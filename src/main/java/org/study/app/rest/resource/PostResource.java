package org.study.app.rest.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.study.app.domain.model.Post;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.PostRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreatePostRequestDTO;
import org.study.app.rest.dto.CreateUserRequestDTO;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final Validator validator;
    private final ObjectMapper mapper;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, Validator validator) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.validator = validator;
        mapper = new ObjectMapper();
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequestDTO requestDTO) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(requestDTO.getText());
        post.setUser(user);
        postRepository.persist(post);

        return Response.status(Response.Status.CREATED.getStatusCode()).build();
    }

    @GET
    public Response listPosts() {
        return Response.ok().build();
    }
}
