package org.study.app.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {

  @NotBlank(message = "Name is required")
  private String name;
  @NotNull(message = "Age is required")
  private Integer age;
}
