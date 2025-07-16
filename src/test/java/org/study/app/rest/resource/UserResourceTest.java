package org.study.app.rest.resource;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.study.app.domain.model.User;
import org.study.app.rest.dto.ResponseError;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

  @TestHTTPResource("/users")
  URL apiUrl;

  @Test
  @Order(1)
  @DisplayName("Should create user successfully")
  public void createUserTest() {
    var user = User.builder()
        .name("Fulano")
        .age(30)
        .build();

    var response = given()
        .contentType(ContentType.JSON).body(user)
        .when()
        .post(apiUrl)
        .then()
        .extract().response();

    assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Must be equals");
    assertNotNull(response.jsonPath().getString("id"), "Must not be null");
  }

  @Test
  @Order(2)
  @DisplayName("Should throw error while trying to create user wrongly")
  public void createUserErrorTest() {
    var user = User.builder()
        .name(null)
        .age(null)
        .build();

    var response = given()
        .contentType(ContentType.JSON).body(user)
        .when()
        .post(apiUrl)
        .then()
        .extract().response();

    assertEquals(ResponseError.UNPROCESSABLE_ENTITY, response.statusCode());
    assertEquals("Validation Errors", response.jsonPath().getString("message"));

    List<Map<String, Object>> errors = response.jsonPath().getList("errors");
    assertNotNull(errors.get(0).get("message"), "Must not be null");
    assertNotNull(errors.get(1).get("message"), "Must not be null");
//    assertEquals("Name is required", errors.get(1).get("message"), "Must be equals"); // NOT IDEAL, ORDER IS RANDOM
  }

  @Test
  @Order(3)
  @DisplayName("Should list all users")
  public void listAllUsesTest() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(apiUrl)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("size()", Matchers.is(1));
  }

}