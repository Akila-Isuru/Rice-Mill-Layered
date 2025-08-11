package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

import java.sql.Date; // Using java.sql.Date as in your original code
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SalesOrderdto {
    private String orderId;
    private String customerId;
    private Date orderDate;
    private int orderAmount;
    private ArrayList<SalesOrderDetailsdto> cartList;


    public SalesOrderdto(String id) {
        this.orderId = id;
    }


    public SalesOrderdto(String orderId, String customerId, java.sql.Date orderDate, int orderAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.orderAmount = orderAmount;
    }
}