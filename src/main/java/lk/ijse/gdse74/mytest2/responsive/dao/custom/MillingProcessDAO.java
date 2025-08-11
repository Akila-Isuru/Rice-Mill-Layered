package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess;

import java.sql.SQLException;
import java.util.Optional;

public interface MillingProcessDAO extends CrudDAO<MillingProcess, String> {

    boolean existsMillingProcessByPaddyId(String paddyId) throws SQLException;

    Optional<MillingProcess> findByPaddyId(String paddyId) throws SQLException;
}