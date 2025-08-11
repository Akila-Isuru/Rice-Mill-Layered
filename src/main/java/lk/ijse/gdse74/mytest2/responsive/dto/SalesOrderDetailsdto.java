package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@ToString
public class SalesOrderDetailsdto {
    private String orderId;
    private String productId;
    private int unitPrice;
    private int qty;
    private int totalPrice;

    // Explicit constructor to match your usage in Controller's btnOnActionPlaceOrder
    public SalesOrderDetailsdto(String orderId, String productId, int qty, int unitPrice, int totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty; // Note the order of qty and unitPrice in your constructor call
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}