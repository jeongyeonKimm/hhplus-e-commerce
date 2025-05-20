package kr.hhplus.be.server.domain.salesranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesRankingKey {

    private static final String SALES_DAILY_KEY = "sales:daily:";
    private static final String LATEST_3DAYS_SALES_KEY = "sales:3days:";

    public static String getSalesDailyKey(LocalDate date) {
        return SALES_DAILY_KEY + date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String getLatest3daysSalesKey(LocalDate date) {
        return LATEST_3DAYS_SALES_KEY + date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
