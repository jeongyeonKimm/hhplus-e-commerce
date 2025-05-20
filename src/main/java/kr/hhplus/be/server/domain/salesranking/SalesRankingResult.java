package kr.hhplus.be.server.domain.salesranking;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
public class SalesRankingResult {

    private final List<Long> productIds;
    private final Set<ZSetOperations.TypedTuple<String>> topTuples;

    private SalesRankingResult(List<Long> productIds, Set<ZSetOperations.TypedTuple<String>> topTuples) {
        this.productIds = productIds;
        this.topTuples = topTuples;
    }

    public static SalesRankingResult from(Set<ZSetOperations.TypedTuple<String>> tuples) {
        if (tuples == null || tuples.isEmpty()) {
            return new SalesRankingResult(Collections.emptyList(), Collections.emptySet());
        }

        List<Long> productIds = tuples.stream()
                .map(t -> extractProductId(t.getValue()))
                .toList();

        return new SalesRankingResult(productIds, tuples);
    }

    public boolean isEmpty() {
        return productIds.isEmpty();
    }

    public static Long extractProductId(String value) {
        if (value == null || !value.startsWith("product:")) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return Long.valueOf(value.substring("product:".length()));
    }
}
