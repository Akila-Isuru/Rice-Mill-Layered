package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal; // Import for BigDecimal
import java.sql.Time;       // Use java.sql.Time for the entity to match DB 'time' type

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MillingProcess {
    private String millingId;
    private String paddyId;
    private Time startTime;
    private Time endTime;
    private BigDecimal milledQuantity;  // Matches 'milled_quantity' (decimal(10,2))
    private BigDecimal brokenRice;      // Matches 'broken_rice' (decimal(10,2))
    private BigDecimal huskKg;          // Matches 'husk_kg' (decimal(10,2)) - Crucial: camelCase for DB's snake_case
    private BigDecimal branKg;          // Matches 'bran_kg' (decimal(10,2)) - Crucial: camelCase for DB's snake_case
}