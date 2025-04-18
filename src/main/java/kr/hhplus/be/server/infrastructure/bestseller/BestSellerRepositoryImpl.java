package kr.hhplus.be.server.infrastructure.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.BestSellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class BestSellerRepositoryImpl implements BestSellerRepository {

    private final BestSellerJpaRepository bestSellerJpaRepository;

    @Override
    public List<BestSeller> getBestSellers() {
        return bestSellerJpaRepository.getBestSellers();
    }

    @Override
    public BestSeller save(BestSeller bestSeller) {
        return bestSellerJpaRepository.save(bestSeller);
    }

    @Override
    public void deleteByCreatedAtBefore(LocalDateTime threshold) {
        bestSellerJpaRepository.deleteByCreatedAtBefore(threshold);
    }
}
