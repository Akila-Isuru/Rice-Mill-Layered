package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MillingProcessdto {
    private String millingId;
    private String paddyId;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal milledQuantity;
    private BigDecimal brokenRice;
    private BigDecimal husk;
    private BigDecimal bran;

    public MillingProcessdto(String id) {
        this.millingId = id;
    }
}