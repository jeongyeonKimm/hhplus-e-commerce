package kr.hhplus.be.server.domain.bestseller;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BestSeller extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String title;

    private byte[] description;

    private Long price;

    private Long stock;

    private Long sales;

    private BestSeller(Long productId, String title, byte[] description, Long price, Long stock, Long sales) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
    }
}
