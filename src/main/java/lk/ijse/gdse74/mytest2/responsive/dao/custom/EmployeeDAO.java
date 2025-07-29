package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Employee;

import java.sql.SQLException;

public interface EmployeeDAO extends CrudDAO<Employee, String> {
    // Add any Employee-specific DAO methods here if needed, beyond standard CRUD
    int getEmployeeCount() throws SQLException; // Moved from model
}