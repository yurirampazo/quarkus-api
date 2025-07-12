package org.study.app.rest.dto;

import lombok.Builder;
import lombok.Data;
import org.study.app.domain.model.Follower;
import org.study.app.domain.model.Post;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Data
@Builder
public class FollowerResponseDTO {
  private Long userId;
  private Long followerId;
  private String name;

  public static FollowerResponseDTO mapDtoFromModel(Follower follower) {
    if (follower == null) {
      return FollowerResponseDTO.builder().build();
    }
    return FollowerResponseDTO.builder()
          .userId(Optional.ofNullable(follower.getFollower())
                .map(f -> Optional.ofNullable(f.getId()).orElse(null))
                .orElse(null))
          .followerId(follower.getId() == null ? 0L : follower.getId())
          .name(Optional.ofNullable(follower.getFollower())
                .map(f -> Optional.ofNullable(f.getName()).orElse(null))
                .orElse(null))
          .build();
  }
}
