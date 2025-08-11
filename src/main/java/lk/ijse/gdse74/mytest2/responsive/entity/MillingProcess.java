package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;

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
    private BigDecimal milledQuantity;
    private BigDecimal brokenRice;
    private BigDecimal huskKg;
    private BigDecimal branKg;
}