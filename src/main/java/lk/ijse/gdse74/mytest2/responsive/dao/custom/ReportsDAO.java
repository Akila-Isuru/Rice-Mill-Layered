package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.ReportsEntity;

import java.sql.SQLException;

public interface ReportsDAO extends CrudDAO<ReportsEntity,String> {

    ReportsEntity search(String id) throws SQLException, ClassNotFoundException;
}