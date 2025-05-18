package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {

    Boolean existById(Long userId);

    User save(User user);

    Optional<User> findById(Long userId);
}
