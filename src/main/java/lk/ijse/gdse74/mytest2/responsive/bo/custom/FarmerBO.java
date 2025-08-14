package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO; // Corrected import

import java.sql.SQLException;
import java.util.List;

public interface FarmerBO extends SuperBO {
    List<FarmerDTO> getAllFarmers() throws SQLException;
    void saveFarmer(FarmerDTO dto) throws DuplicateException, Exception;
    void updateFarmer(FarmerDTO dto) throws SQLException, DuplicateException;
    boolean deleteFarmer(String id) throws InUseException, Exception;
    String getNextId() throws SQLException;

    List<String> getAllFarmerIds() throws SQLException;
}