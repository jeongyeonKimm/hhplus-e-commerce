package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderProductList {

    private List<OrderProduct> orderProducts;

    public int calculateTotalAmount() {
        int totalAmount = 0;
        for (OrderProduct orderProduct : orderProducts) {
            totalAmount += (orderProduct.getAmount() * orderProduct.getQuantity());
        }

        return totalAmount;
    }
}
