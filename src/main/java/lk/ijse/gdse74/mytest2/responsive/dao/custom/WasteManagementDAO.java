package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.WasteManagement;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WasteManagementDAO extends CrudDAO<WasteManagement, String> {
    String generateNextId() throws SQLException, ClassNotFoundException;
}