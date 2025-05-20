package kr.hhplus.be.server.domain.salesranking;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class SalesPeriod {

    private final List<String> dailyKeys;
    private final String aggregatedKey;

    private SalesPeriod(List<String> dailyKeys, String aggregatedKey) {
        this.dailyKeys = dailyKeys;
        this.aggregatedKey = aggregatedKey;
    }

    public static SalesPeriod of(List<String> dailyKeys, String aggregatedKey) {
        return new SalesPeriod(dailyKeys, aggregatedKey);
    }

    public static SalesPeriod lastThreeDays(LocalDate today) {
        List<String> keys = IntStream.rangeClosed(0, 2)
                .mapToObj(i -> SalesRankingKey.getSalesDailyKey(today.minusDays(i)))
                .toList();

        String aggregatedKey = SalesRankingKey.getLatest3daysSalesKey(today);
        return SalesPeriod.of(keys, aggregatedKey);
    }
}
