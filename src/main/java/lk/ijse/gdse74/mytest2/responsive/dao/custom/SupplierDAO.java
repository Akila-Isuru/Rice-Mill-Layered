package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier; // Import the new entity

import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

public interface SupplierDAO extends CrudDAO<Supplier> {
    // Add any specific DAO methods for Supplier if needed
    // Based on your controller, searching by contact or email might be useful for duplicate checks
    Optional<Supplier> findSupplierByContactNumber(String contactNumber) throws SQLException;
    Optional<Supplier> findSupplierByEmail(String email) throws SQLException;
}