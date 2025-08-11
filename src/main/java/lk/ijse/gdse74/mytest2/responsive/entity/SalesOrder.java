package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;
import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class SalesOrder {
    private String orderId;
    private String customerId;
    private Date orderDate;
    private int orderAmount;

    public SalesOrder(String orderId, String customerId, Date orderDate, int orderAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.orderAmount = orderAmount;
    }
}