package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Employeedto;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeBO extends SuperBO {
    List<Employeedto> getAllEmployees() throws SQLException;
    void saveEmployee(Employeedto dto) throws DuplicateException, Exception;
    void updateEmployee(Employeedto dto) throws NotFoundException, Exception;
    boolean deleteEmployee(String id) throws InUseException, NotFoundException, Exception;
    String getNextId() throws SQLException;
    List<String> getAllEmployeeIds() throws SQLException; // For dropdowns/lookups
    int getEmployeeCount() throws Exception;
    Employeedto searchEmployee(String employeeId) throws SQLException, NotFoundException; // NEW: Added this method
}