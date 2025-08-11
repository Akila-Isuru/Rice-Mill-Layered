package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Employee;

import java.sql.SQLException;

public interface EmployeeDAO extends CrudDAO<Employee, String> {
    int getEmployeeCount() throws SQLException;
}