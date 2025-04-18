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
        return jpaQueryFactory.selectFrom(bestSeller)
                .orderBy(bestSeller.sales.desc())
                .limit(5)
                .fetch();
    }
}
