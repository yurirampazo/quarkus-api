package org.study.app.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.study.app.domain.model.Post;
import org.study.app.domain.model.User;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
}
