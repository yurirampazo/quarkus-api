package org.study.app.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.study.app.domain.model.Follower;
import org.study.app.domain.model.User;

import java.sql.ParameterMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {
  public boolean follows(User follower, User user) {

    /*
     * Could be done like this with panache, no need to declare the SQL
     * Better way to do it uncommented next
     * */
//    var params = new HashMap<String, Object>();
//    params.put("follower", follower);
//    params.put("user", user);

    /*Preferred solution*/
    Map<String, Object> params = Parameters.with("follower", follower).and("user", user).map();
    PanacheQuery<Follower> followerPanacheQuery = find("follower =:follower and user =:user", params);
    var followerResult = followerPanacheQuery.firstResultOptional();

    return followerResult.isPresent();
  }

  public List<Follower> findAllFollowersByUserId(Long userId) {
    PanacheQuery<Follower> followerPanacheQuery = find("user.id", userId);
    return followerPanacheQuery.list();
  }

  public void unfollowUserById(Long followerId, Long userId) {
    var params = Parameters
          .with("userId", userId)
          .and("followerId", followerId)
          .map();

    delete("follower.id =: followerId and user.id =: userId", params);
  }
}
