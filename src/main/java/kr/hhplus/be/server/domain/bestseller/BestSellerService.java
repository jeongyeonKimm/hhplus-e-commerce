package kr.hhplus.be.server.domain.bestseller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BestSellerService {

    private final BestSellerRepository bestSellerRepository;

    public List<BestSeller> getBestSellers() {
        return bestSellerRepository.getBestSellers();
    }
}
