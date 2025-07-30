package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Time; // Use java.sql.Time for the entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MillingProcess {
    private String millingId;
    private String paddyId;
    private Time startTime; // Mapped to SQL TIME
    private Time endTime;   // Mapped to SQL TIME
    private BigDecimal milledQuantity;
    private BigDecimal brokenRice;
    private BigDecimal huskKg; // Matches database column name
    private BigDecimal branKg; // Matches database column name
}