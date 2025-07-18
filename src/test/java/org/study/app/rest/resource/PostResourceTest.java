package org.study.app.rest.resource;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.study.app.domain.model.Follower;
import org.study.app.domain.model.Post;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.FollowerRepository;
import org.study.app.domain.repository.PostRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.CreatePostRequestDTO;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

  @Inject
  UserRepository userRepository;

  @Inject
  FollowerRepository followerRepository;

  @Inject
  PostRepository postRepository;

  @BeforeEach
  @Transactional
  void setUp() {
    var user = User.builder()
        .age(30)
        .name("M D Luffy")
        .build();

    var nonFollower = User.builder()
        .age(30)
        .name("R Zoro")
        .build();

    var follower = User.builder()
        .age(30)
        .name("Sanji")
        .build();

    userRepository.persist(user);
    userRepository.persist(nonFollower);
    userRepository.persist(follower);

    Follower followerModel = Follower.builder()
        .user(user)
        .follower(follower)
        .build();
    followerRepository.persist(followerModel);

    postRepository.persist(Post.builder()
        .user(user)
        .text("Inception is the best movie in the world")
        .build());
  }

  @Test
  @DisplayName("Should create a post for an user")
  void createPostTest() {
    var postRequest = CreatePostRequestDTO.builder()
        .text("some text")
        .build();
    var userId = 1;

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(postRequest)
        .pathParam("userId", userId)
        .when()
        .post()
        .then().statusCode(HttpStatus.SC_CREATED);
  }

  @Test
  @DisplayName("Should throw error when trying to create a post for a non existing user")
  void createPostErrorUserNotFoundTest() {
    var postRequest = CreatePostRequestDTO.builder()
        .text("some text")
        .build();
    var userId = 99;

    given()
        .body(postRequest)
        .pathParam("userId", userId)
        .when()
        .post()
        .then()
        .statusCode(SC_NOT_FOUND);
  }

  @Test
  @DisplayName("Should throw error when trying to list a non registered user posts")
  void listPostsNotFoundTest() {
    var userId = 99;

    given()
        .pathParam("userId", userId)
        .when()
        .get()
        .then()
        .statusCode(SC_NOT_FOUND);
  }

  @Test
  @DisplayName("Should throw error when trying to list registered user posts but without passing a follower id")
  void listPostsNoFollowerHeaderTest() {
    var userId = 1;

    var response = given()
        .pathParam("userId", userId)
        .when()
        .get()
        .then()
        .extract().response();

    final var NO_FOLLOWER_MESSAGE = "No user found for follower id";

    assertEquals(NO_FOLLOWER_MESSAGE, response.jsonPath().getString("responseMessage"),
        "Must be equals");
  }

  @Test
  @DisplayName("Should throw error when trying to list registered user posts but with a non existent follower")
  void listPostsFollowerInexistentTest() {
    var userId = 1;
    var followerId = 777;

    var response = given()
        .pathParam("userId", userId)
        .header("followerId", followerId)
        .when()
        .get()
        .then()
        .extract().response();

    final var NO_FOLLOWER_MESSAGE = "No user found for follower id";

    assertEquals(NO_FOLLOWER_MESSAGE, response.jsonPath().getString("responseMessage"),
        "Must be equals");
  }

  @Test
  @DisplayName("Should throw error when trying to list registered user but trying with a non follower")
  void listPostsFollowerNotFoundTest() {
    var userId = 1;
    var followerId = 2;

    var response = given()
        .pathParam("userId", userId)
        .header("followerId", followerId)
        .when()
        .get()
        .then()
        .extract().response();

    final var FORBIDDEN_MESSAGE = "You must follow the user to see it's posts";

    assertEquals(FORBIDDEN_MESSAGE, response.jsonPath().getString("responseMessage"),
        "Must be equals");
  }

  @Test
  @DisplayName("Should list posts successfully")
  void listPosts() {
    var userId = 1;
    var followerId = 3;

    var response = given()
        .pathParam("userId", userId)
        .header("followerId", followerId)
        .when()
        .get()
        .then()
        .extract().response();

    assertEquals(HttpStatus.SC_OK, response.statusCode(), "Must be equals");
    assertFalse(response.jsonPath().getList("").isEmpty(), "Must not be empty");
  }

}