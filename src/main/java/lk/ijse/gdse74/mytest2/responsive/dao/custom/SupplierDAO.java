package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier;

import java.sql.SQLException; // Import added for getSupplierCount()

public interface SupplierDAO extends CrudDAO<Supplier, String> {
    int getSupplierCount() throws SQLException; // Exception type specified
}