package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.SupplierBO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SupplierDAO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto; // Using the existing DTO name
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier; // Import the new entity

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierBOImpl implements SupplierBO {

    private final SupplierDAO supplierDAO = DAOFactory.getInstance().getDAO(DAOTypes.SUPPLIER); // Use correct DAO type
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<Suppliersdto> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = supplierDAO.getAll();
        List<Suppliersdto> supplierDTOS = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            supplierDTOS.add(converter.getSuppliersdto(supplier)); // Use converter
        }
        return supplierDTOS;
    }

    @Override
    public void saveSupplier(Suppliersdto dto) throws DuplicateException, Exception {
        // Check for duplicate ID
        Optional<Supplier> optionalSupplier = supplierDAO.findById(dto.getSupplierId());
        if (optionalSupplier.isPresent()) {
            throw new DuplicateException("Duplicate supplier ID");
        }

        // Check for duplicate contact number
        Optional<Supplier> supplierByContactOptional = supplierDAO.findSupplierByContactNumber(dto.getContactNumber());
        if (supplierByContactOptional.isPresent()) {
            throw new DuplicateException("Duplicate supplier contact number");
        }

        // Check for duplicate email
        Optional<Supplier> supplierByEmailOptional = supplierDAO.findSupplierByEmail(dto.getEmail());
        if (supplierByEmailOptional.isPresent()) {
            throw new DuplicateException("Duplicate supplier email");
        }

        Supplier supplier = converter.getSupplier(dto); // Use converter
        supplierDAO.save(supplier);
    }

    @Override
    public void updateSupplier(Suppliersdto dto) throws SQLException, DuplicateException {
        Optional<Supplier> optionalSupplier = supplierDAO.findById(dto.getSupplierId());
        if (optionalSupplier.isEmpty()) {
            throw new NotFoundException("Supplier not found");
        }

        // Check for duplicate contact number (excluding current supplier)
        Optional<Supplier> supplierByContactOptional = supplierDAO.findSupplierByContactNumber(dto.getContactNumber());
        if (supplierByContactOptional.isPresent()) {
            Supplier existingSupplier = supplierByContactOptional.get();
            if (!existingSupplier.getSupplierId().equals(dto.getSupplierId())) {
                throw new DuplicateException("Duplicate contact number");
            }
        }

        // Check for duplicate email (excluding current supplier)
        Optional<Supplier> supplierByEmailOptional = supplierDAO.findSupplierByEmail(dto.getEmail());
        if (supplierByEmailOptional.isPresent()) {
            Supplier existingSupplier = supplierByEmailOptional.get();
            if (!existingSupplier.getSupplierId().equals(dto.getSupplierId())) {
                throw new DuplicateException("Duplicate email");
            }
        }

        Supplier supplier = converter.getSupplier(dto); // Use converter
        supplierDAO.update(supplier);
    }

    @Override
    public boolean deleteSupplier(String id) throws InUseException, Exception {
        Optional<Supplier> optionalSupplier = supplierDAO.findById(id);
        if (optionalSupplier.isEmpty()) {
            throw new NotFoundException("Supplier not found..!");
        }
        try {
            return supplierDAO.delete(id);
        } catch (SQLException e) {
            // Check SQL error codes for foreign key constraints if possible for more specific error
            throw new InUseException("Cannot delete supplier, it is currently in use or associated with other data.");
        } catch (Exception e) {
            throw new Exception("Error deleting supplier: " + e.getMessage());
        }
    }

    @Override
    public String getNextId() throws SQLException {
        return supplierDAO.getNextId();
    }
}