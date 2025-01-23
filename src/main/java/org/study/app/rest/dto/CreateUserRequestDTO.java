package org.study.app.rest.dto;

public class CreateUserRequestDTO {
  private String name;
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
