package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.dto.FinishedProductdto;

import java.sql.SQLException;
import java.util.List;

public interface FinishedProductBO extends SuperBO {
    List<FinishedProductdto> getAllFinishedProducts() throws SQLException;
    void saveFinishedProduct(FinishedProductdto dto) throws DuplicateException, SQLException;
    void updateFinishedProduct(FinishedProductdto dto) throws SQLException;
    boolean deleteFinishedProduct(String id) throws InUseException, SQLException;
    String getNextFinishedProductId() throws SQLException;
    FinishedProductdto findFinishedProductById(String id) throws SQLException; // For retrieving a single product
    List<String> getAllFinishedProductIds() throws SQLException; // For populating ComboBoxes
    void reduceFinishedProductQuantity(String productId, int quantityToReduce) throws SQLException; // For sales
}