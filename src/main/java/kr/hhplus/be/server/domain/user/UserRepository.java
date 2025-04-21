package kr.hhplus.be.server.domain.user;

public interface UserRepository {

    Boolean existById(Long userId);

    User save(User user);
}
