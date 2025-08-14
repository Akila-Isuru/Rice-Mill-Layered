package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.FinishedProductBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.InventoryBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.FinishedProductDAO;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.InventoryDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.FinishedProductdto;
import lk.ijse.gdse74.mytest2.responsive.dto.Inventorydto;
import lk.ijse.gdse74.mytest2.responsive.entity.FinishedProduct;
import lk.ijse.gdse74.mytest2.responsive.entity.Inventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryBOImpl implements InventoryBO {

    private final InventoryDAO inventoryDAO = DAOFactory.getInstance().getDAO(DAOTypes.INVENTORY);
    private final FinishedProductDAO finishedProductDAO = DAOFactory.getInstance().getDAO(DAOTypes.FINISHED_PRODUCT);

    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<Inventorydto> getAllInventoryItems() throws SQLException {
        List<Inventory> inventoryList = inventoryDAO.getAll();
        List<Inventorydto> dtos = new ArrayList<>();
        for (Inventory item : inventoryList) {
            dtos.add(converter.getInventorydto(item));
        }
        return dtos;
    }

    @Override
    public void saveInventoryItem(Inventorydto dto) throws DuplicateException, SQLException {
        Optional<Inventory> optionalInventory = inventoryDAO.findById(dto.getId());
        if (optionalInventory.isPresent()) {
            throw new DuplicateException("Duplicate Inventory ID: " + dto.getId());
        }
        Inventory entity = converter.getInventory(dto);
        boolean saved = inventoryDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save inventory item.");
        }
    }

    @Override
    public void updateInventoryItem(Inventorydto dto) throws SQLException {
        Optional<Inventory> optionalInventory = inventoryDAO.findById(dto.getId());
        if (optionalInventory.isEmpty()) {
            throw new NotFoundException("Inventory item not found with ID: " + dto.getId());
        }
        Inventory entity = converter.getInventory(dto);
        boolean updated = inventoryDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update inventory item.");
        }
    }

    @Override
    public boolean deleteInventoryItem(String id) throws InUseException, SQLException {
        Optional<Inventory> optionalInventory = inventoryDAO.findById(id);
        if (optionalInventory.isEmpty()) {
            throw new NotFoundException("Inventory item not found with ID: " + id);
        }
        try {
            return inventoryDAO.delete(id);
        } catch (SQLException e) {
            // Check for foreign key constraint violation if inventory is linked to other modules
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Inventory ID: " + id + " is linked to other records and cannot be deleted.");
            }
            throw new SQLException("Error deleting inventory item: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextInventoryId() throws SQLException {
        return inventoryDAO.getNextId();
    }

    @Override
    public Inventorydto findInventoryItemById(String id) throws SQLException {
        Optional<Inventory> optionalInventory = inventoryDAO.findById(id);
        return optionalInventory.map(converter::getInventorydto).orElse(null);
    }

    @Override
    public List<String> getAllInventoryIds() throws SQLException {
        return inventoryDAO.getAllIds();
    }

    @Override
    public List<String> getAllFinishedProductIdsForInventory() throws SQLException {
        // Here, we directly use FinishedProductDAO. You could also get it from FinishedProductBO
        return finishedProductDAO.getAllIds();
    }

    @Override
    public int getFinishedProductCurrentQuantity(String productId) throws SQLException {
        Optional<FinishedProduct> optionalFinishedProduct = finishedProductDAO.findById(productId);
        if (optionalFinishedProduct.isPresent()) {
            return optionalFinishedProduct.get().getTotalQuantityBags();
        }
        throw new NotFoundException("Finished Product not found with ID: " + productId);
    }
}