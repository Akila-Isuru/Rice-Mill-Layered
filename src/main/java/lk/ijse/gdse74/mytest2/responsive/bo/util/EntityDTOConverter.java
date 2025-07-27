package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO;
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto;
import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto; // Import MillingProcessdto
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto;
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer;
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess; // Import MillingProcess entity
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

    public Suppliersdto getSuppliersdto(Supplier supplier) {
        return new Suppliersdto(
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getContactNumber(),
                supplier.getAddress(),
                supplier.getEmail()
        );
    }

    public Supplier getSupplier(Suppliersdto dto) {
        return new Supplier(
                dto.getSupplierId(),
                dto.getName(),
                dto.getContactNumber(),
                dto.getAddress(),
                dto.getEmail()
        );
    }

    // --- New methods for MillingProcess ---

    public MillingProcessdto getMillingProcessdto(MillingProcess process) {
        // Mapping entity fields (husk_kg, bran_kg) to DTO fields (husk, bran)
        return new MillingProcessdto(
                process.getMillingId(),
                process.getPaddyId(),
                process.getStartTime(),
                process.getEndTime(),
                process.getMilledQuantity(),
                process.getBrokenRice(),
                process.getHusk_kg(), // Entity field husk_kg maps to DTO husk
                process.getBran_kg()  // Entity field bran_kg maps to DTO bran
        );
    }

    public MillingProcess getMillingProcess(MillingProcessdto dto) {
        // Mapping DTO fields (husk, bran) to entity fields (husk_kg, bran_kg)
        return new MillingProcess(
                dto.getMillingId(),
                dto.getPaddyId(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getMilledQuantity(),
                dto.getBrokenRice(),
                dto.getHusk(), // DTO field husk maps to Entity husk_kg
                dto.getBran()  // DTO field bran maps to Entity bran_kg
        );
    }
}