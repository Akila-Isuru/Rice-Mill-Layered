package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.dto.Inventorydto;

import java.sql.SQLException;
import java.util.List;

public interface InventoryBO extends SuperBO {
    List<Inventorydto> getAllInventoryItems() throws SQLException;
    void saveInventoryItem(Inventorydto dto) throws DuplicateException, SQLException;
    void updateInventoryItem(Inventorydto dto) throws SQLException;
    boolean deleteInventoryItem(String id) throws InUseException, SQLException;
    String getNextInventoryId() throws SQLException;
    Inventorydto findInventoryItemById(String id) throws SQLException; // For retrieving a single item
    List<String> getAllInventoryIds() throws SQLException; // For internal use if needed

    // Method to get all product IDs for the combo box, potentially from FinishedProductBO
    List<String> getAllFinishedProductIdsForInventory() throws SQLException;

    // Method to get a specific finished product's current stock
    int getFinishedProductCurrentQuantity(String productId) throws SQLException;
}