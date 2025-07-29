package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

import java.math.BigDecimal; // Import BigDecimal
import java.time.LocalTime;  // Use LocalTime for DTO

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MillingProcessdto {
    private String millingId;
    private String paddyId;
    private LocalTime startTime;        // Changed to LocalTime
    private LocalTime endTime;          // Changed to LocalTime
    private BigDecimal milledQuantity;  // Changed to BigDecimal
    private BigDecimal brokenRice;      // Changed to BigDecimal
    private BigDecimal husk;            // Changed to BigDecimal (DTO property name for 'husk_kg')
    private BigDecimal bran;            // Changed to BigDecimal (DTO property name for 'bran_kg')

    public MillingProcessdto(String id) {
        this.millingId = id;
    }
}