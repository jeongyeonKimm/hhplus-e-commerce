package kr.hhplus.be.server.interfaces.api.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.dto.command.ChargePointCommand;
import kr.hhplus.be.server.application.point.dto.command.GetPointCommand;
import kr.hhplus.be.server.application.point.dto.result.PointResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointUseRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PointController.class)
class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointFacade pointFacade;

    @MockitoBean
    private PaymentFacade paymentFacade;

    @DisplayName("사용자의 포인트를 충전한다.")
    @Test
    void chargePoint_success() throws Exception {
        User user = Instancio.of(User.class)
                .set(field(User::getId), 1L)
                .create();
        Point point = Instancio.of(Point.class)
                .set(field(Point::getUserId), user.getId())
                .set(field(Point::getBalance), 10000L)
                .create();
        PointChargeRequest request = PointChargeRequest.of(user.getId(), 1000L);

        PointResult result = PointResult.from(point);
        given(pointFacade.charge(any(ChargePointCommand.class))).willReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.balance").value(point.getBalance()));
    }

    @DisplayName("사용자의 포인트를 조회한다.")
    @Test
    void getPoint() throws Exception {
        User user = Instancio.of(User.class)
                .set(field(User::getId), 1L)
                .create();
        Point point = Instancio.of(Point.class)
                .set(field(Point::getUserId), user.getId())
                .set(field(Point::getBalance), 10000L)
                .create();
        PointResult result = PointResult.from(point);
        given(pointFacade.getPoint(any(GetPointCommand.class))).willReturn(result);

        mockMvc.perform(get("/api/v1/points")
                        .param("userId", String.valueOf(user.getId())))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.balance").value(point.getBalance()));
    }

    @DisplayName("사용자의 포인트를 사용한다.")
    @Test
    void usePoint_success() throws Exception {
        Order order = Instancio.of(Order.class)
                .set(field(kr.hhplus.be.server.domain.order.Order::getId), 2L)
                .create();
        PointUseRequest request = PointUseRequest.of(order.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/points/use")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."));
    }
}
