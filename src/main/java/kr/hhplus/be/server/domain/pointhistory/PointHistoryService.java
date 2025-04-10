package kr.hhplus.be.server.domain.pointhistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private Long sequence = 1L;

    public void saveChargeHistory(Long pointId, Integer amount, Integer balance) {
        PointHistory history = PointHistory.charge(generateId(), pointId, amount, balance);

        pointHistoryRepository.save(history);
    }

    public void saveUseHistory(Long pointId, Integer amount, Integer balance) {
        PointHistory history = PointHistory.use(generateId(), pointId, amount, balance);

        pointHistoryRepository.save(history);
    }

    private Long generateId() {
        return sequence++;
    }
}
