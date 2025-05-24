package kr.hhplus.be.server.interfaces.api.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.dto.CouponGetResult;
import kr.hhplus.be.server.application.coupon.dto.command.CouponGetCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.coupon.dto.request.CouponIssueRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CouponController.class)
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponFacade couponFacade;

    @DisplayName("사용자의 쿠폰 목록을 조회한다.")
    @Test
    void getCoupons_success() throws Exception {
        User user = Instancio.of(User.class)
                .set(field(User::getId), 1L)
                .create();
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field(UserCoupon::getId), 2L)
                .set(field(UserCoupon::getUserId), user.getId())
                .create();
        CouponGetResult result = CouponGetResult.from(user.getId(), List.of(userCoupon));
        given(couponFacade.getCoupons(any(CouponGetCommand.class))).willReturn(result);

        mockMvc.perform(get("/api/v1/coupons")
                        .param("userId", String.valueOf(user.getId())))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.coupons[0].id").value(userCoupon.getId()));
    }

    @DisplayName("사용자에게 쿠폰을 발급한다.")
    @Test
    void issueCoupon_success() throws Exception {
        User user = Instancio.of(User.class)
                .set(field(User::getId), 1L)
                .create();
        Coupon coupon = Instancio.of(Coupon.class)
                .set(field(Coupon::getId), 2L)
                .create();
        CouponIssueRequest request = CouponIssueRequest.of(user.getId(), coupon.getId());

        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."));
    }
}
