package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.FinishedProduct;

import java.sql.SQLException;

public interface FinishedProductDAO extends CrudDAO<FinishedProduct, String> {
    // Add specific DAO methods if needed, e.g., to reduce quantity for sales
    boolean reduceQuantity(String productId, int quantityToReduce) throws SQLException;
}