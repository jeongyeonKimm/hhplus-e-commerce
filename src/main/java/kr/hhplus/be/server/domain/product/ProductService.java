package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_PRODUCT;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResult> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResult::from)
                .collect(Collectors.toList());
    }

    public Product getProductWithLock(Long productId) {
        return productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new ApiException(INVALID_PRODUCT));
    }

    public List<Product> getAllProductsByIds(List<Long> productIds) {
        return productRepository.findAllByIds(productIds);
    }
}
