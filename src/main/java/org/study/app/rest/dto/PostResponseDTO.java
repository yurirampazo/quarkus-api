package org.study.app.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.study.app.domain.model.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {

  private String text;
  private String dateTime;

  public static PostResponseDTO mapFromPost(Post post) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    return new PostResponseDTO(post.getText(),
          post.getDateTime().format(formatter));
  }
}
