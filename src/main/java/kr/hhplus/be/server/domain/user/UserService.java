package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean exists(Long userId) {
        return userRepository.existById(userId);
    }
}
