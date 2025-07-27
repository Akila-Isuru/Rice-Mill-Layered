package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance; // Import the new entity

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MachineMaintenanceDAO extends CrudDAO<MachineMaintenance> {
    // No additional specific DAO methods found in original model beyond CrudDAO
    // If you need search or other specific queries later, add them here.
}