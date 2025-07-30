package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Inventory;

public interface InventoryDAO extends CrudDAO<Inventory, String> {
    // Add any specific Inventory-related DAO methods here if needed
    // (e.g., a method to specifically update stock for a product, if not covered by update method)
}