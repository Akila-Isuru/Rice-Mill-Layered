package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.EmployeeDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public List<Employee> getAll() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM employees");
        List<Employee> employees = new ArrayList<>();
        while (rs.next()) {
            employees.add(new Employee(
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("contact_number"),
                    rs.getString("job_role"),
                    rs.getBigDecimal("basic_salary")
            ));
        }
        return employees;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT employee_id FROM employees ORDER BY employee_id DESC LIMIT 1");
        char tableChar = 'E'; // For Employee ID
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.replaceAll("[^\\d]", ""); // Extract only digits
            if (lastIdNumberString.isEmpty()) {
                return tableChar + "001"; // Fallback if no numbers found
            }
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(Employee entity) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO employees (employee_id, name, address, contact_number, job_role, basic_salary) VALUES (?,?,?,?,?,?)",
                entity.getEmployeeId(),
                entity.getName(),
                entity.getAddress(),
                entity.getContactNumber(),
                entity.getJobRole(),
                entity.getBasicSalary()
        );
    }

    @Override
    public boolean update(Employee entity) throws SQLException {
        return SQLUtill.execute(
                "UPDATE employees SET name = ?, address = ?, contact_number = ?, job_role = ?, basic_salary = ? WHERE employee_id = ?",
                entity.getName(),
                entity.getAddress(),
                entity.getContactNumber(),
                entity.getJobRole(),
                entity.getBasicSalary(),
                entity.getEmployeeId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id=?";
        return SQLUtill.execute(sql, id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        ResultSet rs = SQLUtill.execute("SELECT employee_id FROM employees");
        while(rs.next()){
            ids.add(rs.getString("employee_id"));
        }
        return ids;
    }

    @Override
    public Optional<Employee> findById(String id) throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM employees WHERE employee_id = ?", id);
        if (rs.next()) {
            return Optional.of(new Employee(
                    rs.getString("employee_id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("contact_number"),
                    rs.getString("job_role"),
                    rs.getBigDecimal("basic_salary")
            ));
        }
        return Optional.empty();
    }

    @Override
    public int getEmployeeCount() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT COUNT(*) FROM employees");
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}