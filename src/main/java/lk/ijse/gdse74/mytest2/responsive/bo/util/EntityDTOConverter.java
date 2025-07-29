package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.*;

import lk.ijse.gdse74.mytest2.responsive.entity.*;

import java.math.BigDecimal; // Import BigDecimal
import java.sql.Date;       // Import java.sql.Date for conversions
import java.sql.Time;

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

    public Suppliersdto getSuppliersdto(Supplier supplier) {
        return new Suppliersdto(
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getCotactNumber(),
                supplier.getAddress(),
                supplier.getEmail()
        );
    }

    public Supplier getSupplier(Suppliersdto dto) {
        return new Supplier(
                dto.getSupplierId(),
                dto.getName(),
                dto.getCotactNumber(),
                dto.getAddress(),
                dto.getEmail()
        );
    }

    public Employeedto getEmployeedto(Employee employee) {
        return new Employeedto(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getAddress(),
                employee.getContactNumber(),
                employee.getJobRole(),
                employee.getBasicSalary()
        );
    }

    public Employee getEmployee(Employeedto dto) {
        return new Employee(
                dto.getEmployeeId(),
                dto.getName(),
                dto.getAddress(),
                dto.getContactNumber(),
                dto.getJobRole(),
                dto.getBasicSalary()
        );
    }

    // --- New methods for RawPaddy ---
    public RawPaddydto getRawPaddydto(RawPaddy rawPaddy) {
        // Convert BigDecimal to double (loss of precision) if DTO requires double,
        // or ensure DTO also uses BigDecimal
        return new RawPaddydto(
                rawPaddy.getPaddyId(),
                rawPaddy.getSupplierId(),
                rawPaddy.getFarmerId(),
                rawPaddy.getQuantityKg(),          // Now BigDecimal
                rawPaddy.getMoistureLevel(),       // Now BigDecimal
                rawPaddy.getPurchasePricePerKg(),  // Now BigDecimal
                rawPaddy.getPurchaseDate()         // java.sql.Date -> java.util.Date (direct assignment works)
        );
    }

    public RawPaddy getRawPaddy(RawPaddydto dto) {
        // Convert double to BigDecimal if DTO uses double
        return new RawPaddy(
                dto.getPaddyId(),
                dto.getSupplierId(),
                dto.getFarmerId(),
                dto.getQuantity(),          // Now BigDecimal
                dto.getMoisture(),          // Now BigDecimal
                dto.getPurchasePrice(),     // Now BigDecimal
                (Date) dto.getPurchaseDate() // java.util.Date -> java.sql.Date (requires cast if source is util.Date)
        );
    }
    public MillingProcessdto getMillingProcessdto(MillingProcess millingProcess) {
        return new MillingProcessdto(
                millingProcess.getMillingId(),
                millingProcess.getPaddyId(),
                millingProcess.getStartTime() != null ? millingProcess.getStartTime().toLocalTime() : null, // Convert sql.Time to LocalTime
                millingProcess.getEndTime() != null ? millingProcess.getEndTime().toLocalTime() : null,     // Convert sql.Time to LocalTime
                millingProcess.getMilledQuantity(),
                millingProcess.getBrokenRice(),
                millingProcess.getHuskKg(), // Map huskKg (Entity) to husk (DTO)
                millingProcess.getBranKg()  // Map branKg (Entity) to bran (DTO)
        );
    }
    public MillingProcess getMillingProcess(MillingProcessdto millingProcessdto) {
        return new MillingProcess(
                millingProcessdto.getMillingId(),
                millingProcessdto.getPaddyId(),
                millingProcessdto.getStartTime() != null ? Time.valueOf(millingProcessdto.getStartTime()) : null, // Convert LocalTime to sql.Time
                millingProcessdto.getEndTime() != null ? Time.valueOf(millingProcessdto.getEndTime()) : null,     // Convert LocalTime to sql.Time
                millingProcessdto.getMilledQuantity(),
                millingProcessdto.getBrokenRice(),
                millingProcessdto.getHusk(), // Map husk (DTO) to huskKg (Entity)
                millingProcessdto.getBran()  // Map bran (DTO) to branKg (Entity)
        );
    }
}