package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

import java.math.BigDecimal; // Import BigDecimal
import java.util.Date; // java.util.Date for the DTO

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RawPaddydto {
    private String paddyId;
    private String supplierId;
    private String farmerId;
    private BigDecimal quantity;        // Changed to BigDecimal
    private BigDecimal moisture;        // Changed to BigDecimal
    private BigDecimal purchasePrice;   // Changed to BigDecimal
    private Date purchaseDate;          // java.util.Date for DTO

    public RawPaddydto(String paddyId) {
        this.paddyId = paddyId;
    }
}