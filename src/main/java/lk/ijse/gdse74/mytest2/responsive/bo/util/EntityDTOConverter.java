package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;
import lk.ijse.gdse74.mytest2.responsive.dto.Employeedto; // New import
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO;
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto;

import lk.ijse.gdse74.mytest2.responsive.entity.Customer;
import lk.ijse.gdse74.mytest2.responsive.entity.Employee; // New import
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer;
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier;

import java.math.BigDecimal; // Import for BigDecimal

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

    // --- New methods for Employee ---

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
}