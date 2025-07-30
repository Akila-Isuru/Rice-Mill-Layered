package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

// Note: Using String for maintenanceDate and description as per your table schema.
// Consider using java.sql.Date for maintenanceDate in the entity for better type safety
// and consistency with other date fields in your project if possible.
// For now, sticking to varchar(100) -> String as per your table.
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MachineMaintenance {
    private String maintenanceId;
    private String machineName;
    private String maintenanceDate; // Consider java.sql.Date if possible
    private String description;
    private int cost;
}