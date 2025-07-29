package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess;

import java.sql.SQLException;
import java.util.List;

public interface MillingProcessDAO extends CrudDAO<MillingProcess, String> {
    List<String> getAllPaddyIds() throws SQLException;
    boolean isPaddyIdExistsInProcess(String paddyId) throws SQLException;
    // CrudDAO already provides: getAll(), getNextId(), save(), update(), delete(), findById(), getAllIds()
}