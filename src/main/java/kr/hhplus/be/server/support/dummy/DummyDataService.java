package kr.hhplus.be.server.support.dummy;

import kr.hhplus.be.server.domain.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DummyDataService {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    private static final int THREAD_COUNT = 4;
    private static final int BATCH_SIZE = 1000;
    private static final int USER_COUNT = 100_000;
    private static final int POINT_COUNT = 100_000;
    private static final int POINT_HISTORY_COUNT = 1_000_000;
    private static final int PRODUCT_COUNT = 10000;
    private static final int COUPON_COUNT = 1000;
    private static final int USER_COUPON_COUNT = 300_000;
    private static final int ORDER_COUNT = 1_000_000;
    private static final int ORDER_PRODUCT_COUNT = 5_000_000;
    private static final int BEST_SELLER_COUNT = 7200;

    public void batchInsertUsersWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < USER_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertUsers(batchNumber));
        }
    }

    public void batchInsertPointsWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < POINT_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertPoints(batchNumber));
        }
    }

    public void batchInsertPointHistoriesWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < POINT_HISTORY_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertPointHistories(batchNumber));
        }
    }

    public void batchInsertProductsWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < PRODUCT_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertProducts(batchNumber));
        }
    }

    public void batchInsertCouponsWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < COUPON_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertCoupons(batchNumber));
        }
    }

    public void batchInsertUserCouponsWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < USER_COUPON_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertUserCoupons(batchNumber));
        }
    }

    public void batchInsertOrdersWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < ORDER_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertOrders(batchNumber));
        }
    }

    public void batchInsertOrderProductsWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < ORDER_PRODUCT_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertOrderProducts(batchNumber));
        }
    }

    public void batchInsertBestSellersWithMultiThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < BEST_SELLER_COUNT / BATCH_SIZE; i++) {
            long batchNumber = i;
            executorService.submit(() -> insertBestSellers(batchNumber));
        }
    }

    private void insertUsers(long batchNumber) {
        String sql = "INSERT INTO user (id, created_at, updated_at) VALUES (?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            batchParams.add(new Object[]{i, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertPoints(long batchNumber) {
        String sql = "INSERT INTO point (id, user_id, balance, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            long balance = random.nextInt(1_000_001);
            batchParams.add(new Object[]{i, i, balance, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertPointHistories(long batchNumber) {
        String sql = "INSERT INTO point_history (id, point_id, amount, balance, type, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            long pointId = (i % USER_COUNT == 0) ? USER_COUNT : i % USER_COUNT;
            long amount = getRandomAmount();
            long balance = random.nextInt(1_000_001);
            String type = getRandomType();
            batchParams.add(new Object[]{i, pointId, amount, balance, type, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertProducts(long batchNumber) {
        String sql = "INSERT INTO product (id, name, description, price, stock, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            String name = "Product " + i;
            String description = "This is product " + i;
            long price = getRandomPrice();
            long stock = getRandomProductStock();
            batchParams.add(new Object[]{i, name, description, price, stock, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertCoupons(long batchNumber) {
        String sql = "INSERT INTO coupon (id, title, discount_value, discount_type, start_date, end_date, stock, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Date startDate = Date.valueOf(LocalDate.now());
        Date endDate = Date.valueOf(LocalDate.now().plusDays(30));

        for (long i = start; i < end; i++) {
            String title = "Coupon " + i;
            String discountType = getRandomDiscountType();
            long discountValue = getDiscountValue(discountType);
            long stock = getRandomCouponStock();
            batchParams.add(new Object[]{i, title, discountValue, discountType, startDate, endDate, stock, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertUserCoupons(long batchNumber) {
        String sql = "INSERT INTO user_coupon (id, user_id, coupon_id, is_used, issued_at, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            long userId = getRandomId(USER_COUNT);
            long couponId = getRandomId(COUPON_COUNT);
            boolean isUsed = random.nextBoolean();
            Date issuedAt = getRandomIssuedAt();
            batchParams.add(new Object[]{i, userId, couponId, isUsed, issuedAt, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertOrders(long batchNumber) {
        String sql = "INSERT INTO orders (id, user_id, user_coupon_id, is_coupon_applied, total_amount, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            long userId = getRandomId(USER_COUNT);
            boolean applyCoupon = random.nextDouble() < 0.3;
            Long userCouponId = applyCoupon ? getRandomId(USER_COUPON_COUNT) : null;
            boolean isCouponApplied = applyCoupon;
            long totalAmount = getRandomPrice();
            String status = getRandomOrderStatus();
            batchParams.add(new Object[]{i, userId, userCouponId, isCouponApplied, totalAmount, status, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertOrderProducts(long batchNumber) {
        String sql = "INSERT INTO order_product (id, order_id, product_id, price, quantity, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long i = start; i < end; i++) {
            long orderId = getRandomId(ORDER_COUNT);
            long productId = getRandomId(PRODUCT_COUNT);
            long price = getRandomPrice();
            long quantity = getRandomQuantity();
            batchParams.add(new Object[]{i, orderId, productId, price, quantity, now, now});
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void insertBestSellers(long batchNumber) {
        String sql = "INSERT INTO best_seller (id, product_id, title, description, price, stock, sales, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchParams = new ArrayList<>();

        long start = batchNumber * BATCH_SIZE + 1;
        long end = start + BATCH_SIZE;

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Map<Long, Product> productMap = loadAllProducts();

        List<Long> productIds = new ArrayList<>(productMap.keySet());

        for (long i = start; i < end; i++) {
            long productId = productIds.get(random.nextInt(productIds.size()));
            Product product = productMap.get(productId);

            long sales = getRandomSales();
            batchParams.add(new Object[]{
                    i,
                    productId,
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStock(),
                    sales,
                    now,
                    now
            });
        }

        jdbcTemplate.batchUpdate(sql, batchParams);
    }

    private Map<Long, Product> loadAllProducts() {
        String sql = "SELECT id, name, description, price, stock FROM product";

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Product> map = new HashMap<>();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                long price = rs.getLong("price");
                long stock = rs.getLong("stock");

                map.put(id, Product.of(name, description, price, stock));
            }
            return map;
        });
    }

    private String getRandomType() {
        return random.nextBoolean() ? "CHARGE" : "USE";
    }

    private long getRandomAmount() {
        return 100 + random.nextInt(5_000);
    }

    private long getRandomPrice() {
        return (1_000L * (1 + random.nextInt(990)));
    }

    private long getRandomProductStock() {
        return random.nextInt(1001);
    }

    private String getRandomDiscountType() {
        return random.nextBoolean() ? "AMOUNT" : "RATE";
    }

    private long getDiscountValue(String type) {
        if (type.equals("AMOUNT")) {
            return (random.nextInt(10) + 1) * 1000L;
        } else {
            return random.nextInt(46) + 5;
        }
    }

    private long getRandomCouponStock() {
        return random.nextInt(401) + 100;
    }

    private long getRandomId(int bound) {
        return random.nextInt(bound) + 1;
    }

    private Date getRandomIssuedAt() {
        LocalDate randomDate = LocalDate.now().minusDays(random.nextInt(30));
        return Date.valueOf(randomDate);
    }

    private String getRandomOrderStatus() {
        String[] statuses = {"NOT_PAID", "PAID", "EXPIRED"};
        return statuses[random.nextInt(statuses.length)];
    }

    private long getRandomQuantity() {
        return random.nextInt(5) + 1;
    }

    private long getRandomSales() {
        return random.nextInt(500) + 1;
    }
}
