package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public List<Product> findAll() {
        return List.of(
                Product.builder()
                        .id(1L)
                        .name("iPhone 13")
                        .description("Apple iPhone 13".getBytes())
                        .price(1_000_000)
                        .stock(100)
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("iPad Gen5")
                        .description("Apple iPad Gen5".getBytes())
                        .price(1_500_000)
                        .stock(200)
                        .build()
        );
    }
}
