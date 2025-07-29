package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess; // Import the new entity

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MillingProcessDAO extends CrudDAO<MillingProcess> {
    List<String> getAllPaddyIds() throws SQLException;
    boolean isPaddyIdExistsInProcess(String paddyId) throws SQLException;
    // CrudDAO already provides: getAll(), getNextId(), save(), update(), delete(), findById(), getAllIds()
}