package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Inventory {
    private String inventoryId;
    private String productId;
    private int currentStockBags;
    private Date lastUpdated;
}