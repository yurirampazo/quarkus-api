package org.study.app.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserFollowersResponseDTO {
  private Integer followersCount;
  private List<FollowerResponseDTO> userFollowers;
}
