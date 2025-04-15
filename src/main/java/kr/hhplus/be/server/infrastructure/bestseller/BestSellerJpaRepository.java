package kr.hhplus.be.server.infrastructure.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestSellerJpaRepository extends JpaRepository<BestSeller, Long>, BestSellerCustomRepository {
}
