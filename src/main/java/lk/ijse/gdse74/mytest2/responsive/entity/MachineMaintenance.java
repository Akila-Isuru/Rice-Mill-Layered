package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MachineMaintenance {
    private String maintenanceId;
    private String machineName;
    private String maintenanceDate;
    private String description;
    private int cost;
}