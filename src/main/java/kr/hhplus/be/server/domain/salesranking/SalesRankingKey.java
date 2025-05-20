package kr.hhplus.be.server.domain.salesranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesRankingKey {

    private static final String SALES_DAILY_KEY = "sales:daily:";

    public static String getSalesDailyKey(LocalDate today) {
        return SALES_DAILY_KEY + today.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
