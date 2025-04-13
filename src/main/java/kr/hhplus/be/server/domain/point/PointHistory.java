package kr.hhplus.be.server.domain.point;

public class PointHistory {

    private Long id;
    private Long pointId;
    private Long amount;
    private Long balance;
    private TransactionType type;

    public PointHistory(Long id, Long pointId, Long amount, Long balance, TransactionType type) {
        this.id = id;
        this.pointId = pointId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
    }

    public static PointHistory saveHistory(Long id, Long pointId, Long amount, Long balance, TransactionType type) {
        return new PointHistory(id, pointId, amount, balance, type);
    }
}
