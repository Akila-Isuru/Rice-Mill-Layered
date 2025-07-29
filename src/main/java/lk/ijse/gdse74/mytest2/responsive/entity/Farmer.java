package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Farmer {
    private String farmerId;
    private String name;
    private String contactNumber;
    private String address;
}