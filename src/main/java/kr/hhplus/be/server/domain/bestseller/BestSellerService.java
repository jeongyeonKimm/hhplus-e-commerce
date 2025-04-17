package kr.hhplus.be.server.domain.bestseller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BestSellerService {

    private final BestSellerRepository bestSellerRepository;

    public List<BestSeller> getBestSellers() {
        return bestSellerRepository.getBestSellers();
    }

    public void save(BestSeller bestSeller) {
        bestSellerRepository.save(bestSeller);
    }

    public void deleteByCreatedAtBefore(LocalDateTime threshold) {
        bestSellerRepository.deleteByCreatedAtBefore(threshold);
    }
}
