package kr.hhplus.be.server.infrastructure.bestseller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.QBestSeller;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BestSellerCustomRepositoryImpl implements BestSellerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BestSeller> getBestSellers() {
        QBestSeller bestSeller = QBestSeller.bestSeller;
        List<Long> top5ProductIds = jpaQueryFactory.select(bestSeller.productId)
                .from(bestSeller)
                .groupBy(bestSeller.productId)
                .orderBy(bestSeller.sales.sum().desc())
                .limit(5)
                .fetch();

        return jpaQueryFactory.selectFrom(bestSeller)
                .where(bestSeller.productId.in(top5ProductIds))
                .fetch();
    }
}
