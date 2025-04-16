package kr.hhplus.be.server.domain.bestseller;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "best_seller")
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

    public static BestSeller of(Product product, Long sales) {
        return new BestSeller(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                sales
        );
    }
}
