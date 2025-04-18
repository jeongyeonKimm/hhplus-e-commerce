package kr.hhplus.be.server.infrastructure.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BestSellerJpaRepository extends JpaRepository<BestSeller, Long>, BestSellerCustomRepository {

    void deleteByCreatedAtBefore(LocalDateTime threshold);
}
