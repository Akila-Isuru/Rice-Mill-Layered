package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.WasteManagementdto;

import java.sql.SQLException;
import java.util.List;

public interface WasteManagementBO extends SuperBO {
    String getNextWasteId() throws SQLException, ClassNotFoundException;
    void saveWasteManagement(WasteManagementdto dto) throws DuplicateException, Exception;
    void updateWasteManagement(WasteManagementdto dto) throws NotFoundException, Exception;
    boolean deleteWasteManagement(String id) throws NotFoundException, Exception;
    List<WasteManagementdto> getAllWasteManagement() throws SQLException, ClassNotFoundException;
    WasteManagementdto searchWasteManagement(String wasteId) throws SQLException, ClassNotFoundException, NotFoundException;
}