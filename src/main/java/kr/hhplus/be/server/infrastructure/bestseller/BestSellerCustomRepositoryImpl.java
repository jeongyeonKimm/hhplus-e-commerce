package kr.hhplus.be.server.infrastructure.bestseller;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.bestseller.QBestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerSummaryResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BestSellerCustomRepositoryImpl implements BestSellerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BestSellerSummaryResponse> getBestSellers() {
        QBestSeller bestSeller = QBestSeller.bestSeller;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                BestSellerSummaryResponse.class,
                                bestSeller.productId,
                                bestSeller.title.max(),
                                bestSeller.description.max(),
                                bestSeller.price.max(),
                                bestSeller.stock.max(),
                                bestSeller.sales.sum()
                        )
                )
                .from(bestSeller)
                .groupBy(bestSeller.productId)
                .orderBy(bestSeller.sales.sum().desc())
                .limit(5)
                .fetch();
    }
}
