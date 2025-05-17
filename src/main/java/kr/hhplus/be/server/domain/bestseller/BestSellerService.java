package kr.hhplus.be.server.domain.bestseller;

import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
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

    public BestSellerDto getBestSellers() {
        List<BestSeller> bestSellers = bestSellerRepository.getBestSellers();
        return BestSellerDto.of(bestSellers);
    }

    public void save(BestSeller bestSeller) {
        bestSellerRepository.save(bestSeller);
    }

    public void deleteByCreatedAtBefore(LocalDateTime threshold) {
        bestSellerRepository.deleteByCreatedAtBefore(threshold);
    }
}
