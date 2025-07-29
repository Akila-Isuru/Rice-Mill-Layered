package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.SupplierBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SupplierDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto;
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierBOImpl implements SupplierBO {

    private final SupplierDAO supplierDAO = DAOFactory.getInstance().getDAO(DAOTypes.SUPPLIER);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<Suppliersdto> getAllSuppliers() throws SQLException {
        List<Supplier> entities = supplierDAO.getAll();
        List<Suppliersdto> dtoList = new ArrayList<>();
        for (Supplier entity : entities) {
            dtoList.add(converter.getSuppliersdto(entity));
        }
        return dtoList;
    }

    @Override
    public void saveSupplier(Suppliersdto dto) throws DuplicateException, Exception {
        Optional<Supplier> existing = supplierDAO.findById(dto.getSupplierId());
        if (existing.isPresent()) {
            throw new DuplicateException("Supplier with ID " + dto.getSupplierId() + " already exists.");
        }
        Supplier entity = converter.getSupplier(dto);
        boolean saved = supplierDAO.save(entity);
        if (!saved) {
            throw new Exception("Failed to save supplier.");
        }
    }

    @Override
    public void updateSupplier(Suppliersdto dto) throws NotFoundException, Exception {
        Optional<Supplier> existing = supplierDAO.findById(dto.getSupplierId());
        if (existing.isEmpty()) {
            throw new NotFoundException("Supplier with ID " + dto.getSupplierId() + " not found for update.");
        }
        Supplier entity = converter.getSupplier(dto);
        boolean updated = supplierDAO.update(entity);
        if (!updated) {
            throw new Exception("Failed to update supplier.");
        }
    }

    @Override
    public boolean deleteSupplier(String id) throws InUseException, NotFoundException, Exception {
        Optional<Supplier> existing = supplierDAO.findById(id);
        if (existing.isEmpty()) {
            throw new NotFoundException("Supplier with ID " + id + " not found for deletion.");
        }
        try {
            return supplierDAO.delete(id);
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new InUseException("Cannot delete supplier with ID " + id + "; it is linked to other records (e.g., raw paddy purchases).");
            }
            throw new Exception("Error deleting supplier: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextId() throws SQLException {
        return supplierDAO.getNextId();
    }

    @Override
    public List<String> getAllSupplierIds() throws SQLException {
        return supplierDAO.getAllIds();
    }

    @Override
    public int getSupplierCount() throws Exception {
        return supplierDAO.getSupplierCount();
    }
}