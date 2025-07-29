package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO; // Import FarmerDTO
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer; // Import Farmer entity

public class EntityDTOConverter {

    public Customersdto getCustomerDTO(Customer customer) {
        return new Customersdto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getContactNumber(),
                customer.getAddress(),
                customer.getEmail()
        );
    }

    public Customer getCustomer(Customersdto dto) {
        return new Customer(
                dto.getCustomerId(),
                dto.getName(),
                dto.getContactNumber(),
                dto.getAddress(),
                dto.getEmail()
        );
    }

    // --- New methods for Farmer ---

    public FarmerDTO getFarmerDTO(Farmer farmer) {
        return new FarmerDTO(
                farmer.getFarmerId(),
                farmer.getName(),
                farmer.getContactNumber(),
                farmer.getAddress()
        );
    }

    public Farmer getFarmer(FarmerDTO dto) {
        return new Farmer(
                dto.getFarmerId(),
                dto.getName(),
                dto.getContactNumber(),
                dto.getAddress()
        );
    }
}