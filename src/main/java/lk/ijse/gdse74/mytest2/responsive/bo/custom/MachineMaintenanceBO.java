package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto; // Using the existing DTO name

import java.sql.SQLException;
import java.util.List;

public interface MachineMaintenanceBO extends SuperBO {
    List<MachineMaintenancedto> getAllMachineMaintenance() throws SQLException;
    void saveMachineMaintenance(MachineMaintenancedto dto) throws DuplicateException, Exception;
    void updateMachineMaintenance(MachineMaintenancedto dto) throws SQLException; // Assuming no duplicate check for update here, similar to CustomerBO update
    boolean deleteMachineMaintenance(String id) throws InUseException, Exception;
    String getNextId() throws SQLException;
}