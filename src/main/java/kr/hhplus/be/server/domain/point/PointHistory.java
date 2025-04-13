package kr.hhplus.be.server.domain.point;

public class PointHistory {

    private Long id;
    private Long pointId;
    private Integer amount;
    private Integer balance;
    private TransactionType type;

    public PointHistory(Long id, Long pointId, Integer amount, Integer balance, TransactionType type) {
        this.id = id;
        this.pointId = pointId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
    }

    public static PointHistory charge(Long id, Long pointId, Integer amount, Integer balance) {
        return new PointHistory(id, pointId, amount, balance, TransactionType.CHARGE);
    }

    public static PointHistory use(Long id, Long pointId, Integer amount, Integer balance) {
        return new PointHistory(id, pointId, amount, balance, TransactionType.USE);
    }
}
