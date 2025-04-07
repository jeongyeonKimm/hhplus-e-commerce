package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResult> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResult::from)
                .collect(Collectors.toList());
    }
}
