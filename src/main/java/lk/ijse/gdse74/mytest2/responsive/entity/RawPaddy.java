package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RawPaddy {
    private String paddyId;
    private String supplierId;
    private String farmerId;
    private BigDecimal quantityKg;
    private BigDecimal moistureLevel;
    private BigDecimal purchasePricePerKg;
    private Date purchaseDate;
}