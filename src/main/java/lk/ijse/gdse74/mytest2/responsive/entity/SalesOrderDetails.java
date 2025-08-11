package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SalesOrderDetails {
    private String orderId;
    private String productId;
    private int unitPrice;
    private int qty;
    private int totalPrice;
}