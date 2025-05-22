package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_STOCK;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_RESTORE_QUANTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Long price;

    private Long stock;

    private Product(String name, String description, Long price, Long stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public static Product of(String name, String description, Long price, Long stock) {
        return new Product(name, description, price, stock);
    }

    public void deduct(Long quantity) {
        if (quantity > stock) {
            throw new ApiException(INSUFFICIENT_STOCK);
        }

        this.stock -= quantity;
    }

    public void restore(Long quantity) {
        if (quantity <= 0) {
            throw new ApiException(INVALID_RESTORE_QUANTITY);
        }
        this.stock += quantity;
    }
}
