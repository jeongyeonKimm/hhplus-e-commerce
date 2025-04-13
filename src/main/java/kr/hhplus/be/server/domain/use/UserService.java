package kr.hhplus.be.server.domain.use;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean exists(Long userId) {
        return userRepository.existById(userId);
    }
}
