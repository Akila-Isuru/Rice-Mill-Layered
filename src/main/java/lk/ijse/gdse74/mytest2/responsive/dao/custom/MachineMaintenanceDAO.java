package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance; // Import the new entity


public interface MachineMaintenanceDAO extends CrudDAO<MachineMaintenance, String> {
    // No additional specific DAO methods found in original model beyond CrudDAO
    // If you need search or other specific queries later, add them here.
}