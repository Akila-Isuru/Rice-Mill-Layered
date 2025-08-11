package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FinishedProduct {
    private String productId;
    private String millingId;
    private String productType;
    private BigDecimal packagingSizeKg;
    private int totalQuantityBags;
    private int pricePerBag;

    public FinishedProduct(String productId) {
        this.productId = productId;
    }
}