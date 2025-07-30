package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto;

import java.sql.SQLException;
import java.util.List;

public interface MachineMaintenanceBO extends SuperBO {
    List<MachineMaintenancedto> getAllMachineMaintenance() throws SQLException;
    void saveMachineMaintenance(MachineMaintenancedto dto) throws DuplicateException, SQLException;
    void updateMachineMaintenance(MachineMaintenancedto dto) throws NotFoundException, SQLException;
    boolean deleteMachineMaintenance(String id) throws NotFoundException, InUseException, SQLException;
    String getNextMaintenanceId() throws SQLException;
    MachineMaintenancedto findMachineMaintenanceById(String id) throws SQLException;
}