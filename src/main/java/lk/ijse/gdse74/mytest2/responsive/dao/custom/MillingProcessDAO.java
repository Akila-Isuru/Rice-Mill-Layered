package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess;

import java.sql.SQLException;
import java.util.Optional;

public interface MillingProcessDAO extends CrudDAO<MillingProcess, String> {
    // Add specific DAO methods if needed beyond basic CRUD
    // For example, to check if a paddy ID is already used in a milling process
    boolean existsMillingProcessByPaddyId(String paddyId) throws SQLException;
    // Method to find by paddy ID
    Optional<MillingProcess> findByPaddyId(String paddyId) throws SQLException;
}