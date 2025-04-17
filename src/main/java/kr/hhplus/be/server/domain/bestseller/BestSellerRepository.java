package kr.hhplus.be.server.domain.bestseller;

import java.time.LocalDateTime;
import java.util.List;

public interface BestSellerRepository {

    List<BestSeller> getBestSellers();

    BestSeller save(BestSeller bestSeller);

    void deleteByCreatedAtBefore(LocalDateTime threshold);
}
