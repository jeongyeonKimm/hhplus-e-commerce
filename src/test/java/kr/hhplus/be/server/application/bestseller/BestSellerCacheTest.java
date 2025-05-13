package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.BestSellerService;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.infrastructure.bestseller.BestSellerJpaRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

class BestSellerCacheTest extends IntegrationTestSupport {

    @Autowired
    private BestSellerFacade bestSellerFacade;

    @MockitoBean
    private BestSellerService bestSellerService;

    @Autowired
    private BestSellerJpaRepository bestSellerJpaRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("인기 상품 조회를 하면 첫 요청은 DB에 접근하여 데이터를 조회하고, 이후 요청은 캐시에서 데이터를 조회한다.")
    @Test
    void getBestSellers_cache() {
        IntStream.range(1, 101).forEach(i -> {
            BestSeller bestSeller = Instancio.of(BestSeller.class)
                    .ignore(field(BestSeller.class, "id"))
                    .set(field(BestSeller::getTitle), "BestSeller " + i)
                    .set(field(BestSeller::getDescription), "This is BestSeller " + i)
                    .set(field(BestSeller::getPrice), (long) (Math.random() * 99000) + 1000)
                    .set(field(BestSeller::getStock), 100L * i)
                    .set(field(BestSeller::getSales), 10L * i)
                    .set(field(BestSeller::getUpdatedAt), LocalDateTime.now().minusDays(1))
                    .set(field(BestSeller::getCreatedAt), LocalDateTime.now().minusDays(1))
                    .create();

            bestSellerJpaRepository.saveAndFlush(bestSeller);
        });

        List<BestSeller> bestSellers = List.of(
                Instancio.of(BestSeller.class)
                        .ignore(field(BestSeller::getCreatedAt))
                        .ignore(field(BestSeller::getUpdatedAt))
                        .create(),
                Instancio.of(BestSeller.class)
                        .ignore(field(BestSeller::getCreatedAt))
                        .ignore(field(BestSeller::getUpdatedAt))
                        .create(),
                Instancio.of(BestSeller.class)
                        .ignore(field(BestSeller::getCreatedAt))
                        .ignore(field(BestSeller::getUpdatedAt))
                        .create(),
                Instancio.of(BestSeller.class)
                        .ignore(field(BestSeller::getCreatedAt))
                        .ignore(field(BestSeller::getUpdatedAt))
                        .create(),
                Instancio.of(BestSeller.class)
                        .ignore(field(BestSeller::getCreatedAt))
                        .ignore(field(BestSeller::getUpdatedAt))
                        .create()
        );
        when(bestSellerService.getBestSellers()).thenReturn(Instancio.of(BestSellerDto.class)
                        .set(field(BestSellerDto::getBestSellers), bestSellers)
                .create());

        Object before = redisTemplate.opsForValue().get("bestSellers::best");
        assertThat(before).isNull();

        bestSellerFacade.getBestSellers();

        Object after = redisTemplate.opsForValue().get("bestSellers::best");
        assertThat(after).isNotNull();

        verify(bestSellerService, times(1)).getBestSellers();
    }
}
