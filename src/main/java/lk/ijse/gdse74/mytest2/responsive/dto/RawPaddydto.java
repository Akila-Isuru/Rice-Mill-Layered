package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RawPaddydto {
    private String paddyId;
    private String supplierId;
    private String farmerId;
    private BigDecimal quantity;
    private BigDecimal moisture;
    private BigDecimal purchasePrice;
    private Date purchaseDate;

    public RawPaddydto(String paddyId) {
        this.paddyId = paddyId;
    }
}