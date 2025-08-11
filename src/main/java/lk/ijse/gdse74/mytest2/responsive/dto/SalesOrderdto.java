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
    private Date orderDate; // int totalAmount; as per your original request
    private int orderAmount; // Changed from totalAmount to orderAmount as per your original DTO name
    private ArrayList<SalesOrderDetailsdto> cartList; // Keeping ArrayList as per your original request

    // Your existing constructor
    public SalesOrderdto(String id) {
        this.orderId = id;
    }

    // Constructor to match the data structure used for existing orders (from SalesOrderModel.viewAllSalesOrder)
    public SalesOrderdto(String orderId, String customerId, java.sql.Date orderDate, int orderAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.orderAmount = orderAmount;
    }
}