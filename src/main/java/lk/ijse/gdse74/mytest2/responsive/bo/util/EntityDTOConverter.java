package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO;
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto; // Import MachineMaintenancedto
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer;
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance; // Import MachineMaintenance entity

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

    // --- New methods for MachineMaintenance ---

    public MachineMaintenancedto getMachineMaintenancedto(MachineMaintenance maintenance) {
        return new MachineMaintenancedto(
                maintenance.getMaintenanceId(),
                maintenance.getMachineName(),
                maintenance.getMaintenanceDate(),
                maintenance.getDescription(),
                maintenance.getCost()
        );
    }

    public MachineMaintenance getMachineMaintenance(MachineMaintenancedto dto) {
        return new MachineMaintenance(
                dto.getMaintenanceId(),
                dto.getMachineName(),
                dto.getMaintenanceDate(),
                dto.getDescription(),
                dto.getCost()
        );
    }
}