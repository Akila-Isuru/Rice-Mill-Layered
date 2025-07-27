package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Supplier { // This is the new entity class
    private String supplierId;
    private String name;
    private String contactNumber; // Note: Database column is 'cotact_number'
    private String address;
    private String email;
}