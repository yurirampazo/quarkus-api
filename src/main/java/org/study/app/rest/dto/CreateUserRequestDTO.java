package org.study.app.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequestDTO {

  @NotBlank(message = "Name is required")
  private String name;
  @NotNull(message = "Age is required")
  private Integer age;


  public CreateUserRequestDTO() {
  }

  public CreateUserRequestDTO(String name, Integer age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
