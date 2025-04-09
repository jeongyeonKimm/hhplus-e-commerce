package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderProductList {

    private List<OrderProductInfo> productInfos
            ;

    private OrderProductList(List<OrderProductInfo> productInfos) {
        this.productInfos = productInfos;
    }

    public static OrderProductList create(List<OrderProductInfo> productInfos) {
        return new OrderProductList(productInfos);
    }

    public int calculateTotalAmount() {
        int totalAmount = 0;
        for (OrderProductInfo info : productInfos) {
            totalAmount += (info.getAmount() * info.getQuantity());
        }

        return totalAmount;
    }

    public List<OrderProduct> toOrderProducts() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductInfo info : productInfos) {
            orderProducts.add(info.toOrderProduct());
        }

        return orderProducts;
    }
}
