package lk.ijse.gdse74.mytest2.responsive.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FarmerDTO {
    private String farmerId;
    private String name;
    private String contactNumber;
    private String address;

    public FarmerDTO(String id) {
        this.farmerId = id;
    }
}