package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal; // Import for BigDecimal
import java.sql.Date;       // Use java.sql.Date for the entity to match DB 'date' type

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RawPaddy {
    private String paddyId;
    private String supplierId;
    private String farmerId;
    private BigDecimal quantityKg;          // Matches 'quantity_kg' (decimal(50,0))
    private BigDecimal moistureLevel;       // Matches 'moisture_level' (decimal(50,0))
    private BigDecimal purchasePricePerKg;  // Matches 'purchase_price_per_kg' (decimal(50,0))
    private Date purchaseDate;              // Matches 'purchase_date' (date)
}