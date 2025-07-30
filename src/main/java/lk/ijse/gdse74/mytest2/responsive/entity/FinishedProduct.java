package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal; // Use BigDecimal for exact decimal representation

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FinishedProduct {
    private String productId;
    private String millingId;
    private String productType;
    private BigDecimal packagingSizeKg; // Matches database column type
    private int totalQuantityBags;
    private int pricePerBag;
}