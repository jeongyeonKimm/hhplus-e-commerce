package kr.hhplus.be.server.infrastructure.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;

import java.util.List;

public interface BestSellerCustomRepository {

    List<BestSeller> getBestSellers();
}
