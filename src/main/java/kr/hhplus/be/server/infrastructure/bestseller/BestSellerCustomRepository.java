package kr.hhplus.be.server.infrastructure.bestseller;

import kr.hhplus.be.server.domain.bestseller.dto.BestSellerSummaryResponse;

import java.util.List;

public interface BestSellerCustomRepository {

    List<BestSellerSummaryResponse> getBestSellers();
}
