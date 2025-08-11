package lk.ijse.gdse74.mytest2.responsive.bo.util;

import lk.ijse.gdse74.mytest2.responsive.dto.*;
import lk.ijse.gdse74.mytest2.responsive.entity.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Added for stream usage

public class EntityDTOConverter {

    // ... (Existing conversion methods for Customer, Farmer, Supplier, Employee, RawPaddy, MillingProcess, User, FinishedProduct, Inventory) ...

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

    public RawPaddydto getRawPaddydto(RawPaddy rawPaddy) {
        return new RawPaddydto(
                rawPaddy.getPaddyId(),
                rawPaddy.getSupplierId(),
                rawPaddy.getFarmerId(),
                rawPaddy.getQuantityKg(),
                rawPaddy.getMoistureLevel(),
                rawPaddy.getPurchasePricePerKg(),
                rawPaddy.getPurchaseDate()
        );
    }

    public RawPaddy getRawPaddy(RawPaddydto dto) {
        return new RawPaddy(
                dto.getPaddyId(),
                dto.getSupplierId(),
                dto.getFarmerId(),
                dto.getQuantity(),
                dto.getMoisture(),
                dto.getPurchasePrice(),
                (Date) dto.getPurchaseDate()
        );
    }

    public MillingProcessdto getMillingProcessdto(MillingProcess millingProcess) {
        return new MillingProcessdto(
                millingProcess.getMillingId(),
                millingProcess.getPaddyId(),
                millingProcess.getStartTime() != null ? millingProcess.getStartTime().toLocalTime() : null,
                millingProcess.getEndTime() != null ? millingProcess.getEndTime().toLocalTime() : null,
                millingProcess.getMilledQuantity(),
                millingProcess.getBrokenRice(),
                millingProcess.getHuskKg(),
                millingProcess.getBranKg()
        );
    }

    public MillingProcess getMillingProcess(MillingProcessdto millingProcessdto) {
        return new MillingProcess(
                millingProcessdto.getMillingId(),
                millingProcessdto.getPaddyId(),
                millingProcessdto.getStartTime() != null ? Time.valueOf(millingProcessdto.getStartTime()) : null,
                millingProcessdto.getEndTime() != null ? Time.valueOf(millingProcessdto.getEndTime()) : null,
                millingProcessdto.getMilledQuantity(),
                millingProcessdto.getBrokenRice(),
                millingProcessdto.getHusk(),
                millingProcessdto.getBran()
        );
    }

    public Usersdto getUsersdto(User user) {
        return new Usersdto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getContactNumber()
        );
    }

    public User getUser(Usersdto dto) {
        return new User(
                dto.getUser_id(),
                dto.getName(),
                dto.getEmail(),
                dto.getRole(),
                dto.getContact_number(),
                dto.getPassword()
        );
    }

    public List<Usersdto> toUsersdtoList(List<User> userList) {
        List<Usersdto> dtoList = new ArrayList<>();
        for (User user : userList) {
            dtoList.add(getUsersdto(user));
        }
        return dtoList;
    }

    public List<User> toUserList(List<Usersdto> dtoList) {
        List<User> userList = new ArrayList<>();
        for (Usersdto dto : dtoList) {
            userList.add(getUser(dto));
        }
        return userList;
    }

    public FinishedProductdto getFinishedProductdto(FinishedProduct finishedProduct) {
        return new FinishedProductdto(
                finishedProduct.getProductId(),
                finishedProduct.getMillingId(),
                finishedProduct.getProductType(),
                finishedProduct.getPackagingSizeKg().doubleValue(), // Convert BigDecimal to double for DTO
                finishedProduct.getTotalQuantityBags(),
                finishedProduct.getPricePerBag()
        );
    }

    public FinishedProduct getFinishedProduct(FinishedProductdto finishedProductdto) {
        return new FinishedProduct(
                finishedProductdto.getProductId(),
                finishedProductdto.getMillingId(),
                finishedProductdto.getProductType(),
                BigDecimal.valueOf(finishedProductdto.getPackageSize()), // Convert double to BigDecimal for Entity
                finishedProductdto.getQuantityBags(),
                finishedProductdto.getPricePerBag()
        );
    }

    public Inventorydto getInventorydto(Inventory inventory) {
        return new Inventorydto(
                inventory.getInventoryId(),
                inventory.getProductId(),
                inventory.getCurrentStockBags(),
                inventory.getLastUpdated()
        );
    }

    public Inventory getInventory(Inventorydto inventorydto) {
        return new Inventory(
                inventorydto.getId(),
                inventorydto.getProductId(),
                inventorydto.getCurrentStockBags(),
                inventorydto.getLastUpdated()
        );
    }

    // --- NEW: MachineMaintenance Conversions ---
    public MachineMaintenancedto getMachineMaintenancedto(MachineMaintenance machineMaintenance) {
        return new MachineMaintenancedto(
                machineMaintenance.getMaintenanceId(),
                machineMaintenance.getMachineName(),
                machineMaintenance.getMaintenanceDate(),
                machineMaintenance.getDescription(),
                machineMaintenance.getCost()
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

    // --- NEW: SalesOrder Conversions ---
    public SalesOrderdto getSalesOrderdto(SalesOrder salesOrder) {
        return new SalesOrderdto(
                salesOrder.getOrderId(),
                salesOrder.getCustomerId(),
                salesOrder.getOrderDate(),
                salesOrder.getOrderAmount(), // Mapping totalAmount from entity to orderAmount in DTO
                null // cartList is not stored in SalesOrder entity, needs to be fetched separately if needed
        );
    }

    public SalesOrder getSalesOrder(SalesOrderdto dto) {
        return new SalesOrder(
                dto.getOrderId(),
                dto.getCustomerId(),
                dto.getOrderDate(),
                dto.getOrderAmount() // Mapping orderAmount from DTO to totalAmount for entity/DB
        );
    }

    public List<SalesOrderdto> toSalesOrderdtoList(List<SalesOrder> salesOrderList) {
        return salesOrderList.stream()
                .map(this::getSalesOrderdto)
                .collect(Collectors.toList());
    }

    // --- NEW: SalesOrderDetails Conversions ---
    public SalesOrderDetailsdto getSalesOrderDetailsdto(SalesOrderDetails salesOrderDetails) {
        return new SalesOrderDetailsdto(
                salesOrderDetails.getOrderId(),
                salesOrderDetails.getProductId(),
                salesOrderDetails.getQty(), // Correct order based on your DTO constructor
                salesOrderDetails.getUnitPrice(),
                salesOrderDetails.getTotalPrice()
        );
    }

    public SalesOrderDetails getSalesOrderDetails(SalesOrderDetailsdto dto) {
        return new SalesOrderDetails(
                dto.getOrderId(),
                dto.getProductId(),
                dto.getUnitPrice(),
                dto.getQty(),
                dto.getTotalPrice()
        );
    }

    public List<SalesOrderDetailsdto> toSalesOrderDetailsdtoList(List<SalesOrderDetails> detailsList) {
        return detailsList.stream()
                .map(this::getSalesOrderDetailsdto)
                .collect(Collectors.toList());
    }

    public List<SalesOrderDetails> toSalesOrderDetailsList(List<SalesOrderDetailsdto> dtoList) {
        return dtoList.stream()
                .map(this::getSalesOrderDetails)
                .collect(Collectors.toList());
    }
}