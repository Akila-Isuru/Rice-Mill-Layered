package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MillingProcess { // This is the new entity class
    private String millingId;
    private String paddyId;
    private Time startTime;
    private Time endTime;
    private double milledQuantity;
    private double brokenRice;
    private double husk_kg; // Matches database column name
    private double bran_kg; // Matches database column name
}