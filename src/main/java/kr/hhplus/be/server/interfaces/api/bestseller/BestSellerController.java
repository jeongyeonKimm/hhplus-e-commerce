package kr.hhplus.be.server.interfaces.api.bestseller;

import kr.hhplus.be.server.application.bestseller.BestSellerFacade;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.interfaces.api.bestseller.dto.response.BestProductListResponse;
import kr.hhplus.be.server.interfaces.api.bestseller.dto.response.BestProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/bestsellers")
@RequiredArgsConstructor
@RestController
public class BestSellerController implements BestSellerSpec {

    private final BestSellerFacade bestSellerFacade;

    @GetMapping
    public ApiResponse<BestProductListResponse<BestProductResponse>> getBestProducts() {
        BestSellerDto result = bestSellerFacade.getBestSellers();
        return ApiResponse.success(BestProductListResponse.from(result));
    }
}
