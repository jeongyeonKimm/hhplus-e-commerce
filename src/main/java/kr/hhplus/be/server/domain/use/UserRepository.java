package kr.hhplus.be.server.domain.use;

public interface UserRepository {

    Boolean existById(Long userId);
}
