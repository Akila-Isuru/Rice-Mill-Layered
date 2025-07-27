package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Customer {
    private String customerId;
    private String name;
    private String contactNumber;
    private String address;
    private String email;
}