package org.study.app;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.study.app.domain.model.User;
import org.study.app.domain.repository.UserRepository;

import java.util.Optional;

@ApplicationScoped
public class AppUtils {

  private final UserRepository userRepository;

  @Inject
  public AppUtils(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  public Optional<User> findUserIfExist(Long userId) {
    if (userId == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(userRepository.findById(userId));
  }

  public static boolean isNullOrEmpty(String text) {
    return text != null && !text.isBlank();
  }

}
