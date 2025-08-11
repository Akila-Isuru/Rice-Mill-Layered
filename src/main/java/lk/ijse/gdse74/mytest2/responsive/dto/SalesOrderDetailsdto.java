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


    public SalesOrderDetailsdto(String orderId, String productId, int qty, int unitPrice, int totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}