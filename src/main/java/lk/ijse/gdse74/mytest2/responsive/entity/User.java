package lk.ijse.gdse74.mytest2.responsive.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String contactNumber;
    private String password; // Storing as String, consider hashing for security
}