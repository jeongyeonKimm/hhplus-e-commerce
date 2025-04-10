package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.interfaces.api.product.dto.response.ProductListResponse;
import kr.hhplus.be.server.interfaces.api.product.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@RestController
public class ProductController implements ProductSpec {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<ProductListResponse<ProductResponse>> getProducts() {
        List<ProductResult> results = productService.getAllProducts();
        return ApiResponse.success(ProductListResponse.from(results));
    }

}
