package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReportsEntity {
    private String reportId;
    private String reportType;
    private String reportDate;

    public ReportsEntity(String id) {
        this.reportId = id;
    }
}