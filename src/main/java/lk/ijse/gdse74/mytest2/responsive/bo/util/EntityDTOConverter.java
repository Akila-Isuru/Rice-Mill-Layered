package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO; // Import FarmerDTO
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto;
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto;
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer; // Import Farmer entity
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance;
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier;

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
    public Suppliersdto getSuppliersdto(Supplier supplier) {
        return new Suppliersdto(
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getCotactNumber(), // Changed to getCotactNumber
                supplier.getAddress(),
                supplier.getEmail()
        );
    }
    public Supplier getSupplier(Suppliersdto dto) {
        return new Supplier(
                dto.getSupplierId(),
                dto.getName(),
                dto.getCotactNumber(), // Changed to getCotactNumber
                dto.getAddress(),
                dto.getEmail()
        );
    }


}