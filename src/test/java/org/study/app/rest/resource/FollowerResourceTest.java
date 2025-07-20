package org.study.app.rest.resource;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.study.app.domain.model.Follower;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.FollowerRepository;
import org.study.app.domain.repository.UserRepository;
import org.study.app.rest.dto.FollowerRequestDTO;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

  @Inject
  UserRepository userRepository;

  @Inject
  FollowerRepository followerRepository;

  Long userId;
  Long nonFollowerId;
  Long followerId;

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

    userId = user.getId();
    nonFollowerId = nonFollower.getId();
    followerId = follower.getId();

  }

  @Test
  @DisplayName("Should not allow user to follow itself")
  void sameUserAsFollowerTest() {
    var body = FollowerRequestDTO.builder()
        .followerId(userId)
        .build();

    var response = given()
        .pathParam("userId", userId)
        .body(body)
        .contentType(ContentType.JSON)
        .when()
        .put()
        .then()
        .extract().response();

    final var CONFLICT_MESSAGE = "You can´t follow yourself";

    assertEquals(CONFLICT_MESSAGE, response.jsonPath().getString("responseMessage"),
        "Must be equals");
  }

  @Test
  @DisplayName("User not found test")
  void userNotFoundTest() {
    var userId = 900L;
    var body = FollowerRequestDTO.builder()
        .followerId(followerId)
        .build();

    given()
        .pathParam("userId", userId)
        .body(body)
        .contentType(ContentType.JSON)
        .when()
        .put()
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  @DisplayName("User not found test")
  void shouldFollowAUser() {
    var body = FollowerRequestDTO.builder()
        .followerId(followerId)
        .build();

    given()
        .pathParam("userId", userId)
        .body(body)
        .contentType(ContentType.JSON)
        .when()
        .put()
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  @DisplayName("should return 404 on list user followers and user id does not exist")
  void followerNotFoundTest() {
    var userId = 999L;

    given()
        .pathParam("userId", userId)
        .when()
        .get()
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  @DisplayName("should return an user followers list")
  void listFollowersTest() {
    var response = given()
        .pathParam("userId", userId)
        .when()
        .get()
        .then()
        .extract().response();

    assertEquals(HttpStatus.SC_OK, response.statusCode(), "Must be equals");
    assertFalse(response.jsonPath().getList("userFollowers").isEmpty(),
        "Followers must not be empty");
  }

  @Test
  @DisplayName("should return 404 when trying to unfollow a non followed user")
  void unfollowUserErrorTest() {
    var userId = 999L;

    given()
        .pathParam("userId", userId)
        .queryParam("followerId", followerId)
        .when()
        .delete()
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  @DisplayName("should successfully unfollow an user")
  void unfollowUserTest() {

    given()
        .pathParam("userId", userId)
        .queryParam("followerId", followerId)
        .when()
        .delete()
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

}