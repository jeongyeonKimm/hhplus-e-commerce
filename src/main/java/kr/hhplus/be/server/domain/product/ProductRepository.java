package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product);

    List<Product> findAll();

    Optional<Product> findById(Long productId);

}
