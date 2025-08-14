package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WasteManagement {
    private String wasteId;
    private String millingId;
    private String wasteType;
    private int quantity;
    private String disposalMethod;
    private Date recordDate;
}