package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.EmployeeBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.EmployeeDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Employee;
import lk.ijse.gdse74.mytest2.responsive.dto.Employeedto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeBOImpl implements EmployeeBO {

    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<Employeedto> getAllEmployees() throws SQLException {
        List<Employee> entities = employeeDAO.getAll();
        List<Employeedto> dtoList = new ArrayList<>();
        for (Employee entity : entities) {
            dtoList.add(converter.getEmployeedto(entity));
        }
        return dtoList;
    }

    @Override
    public void saveEmployee(Employeedto dto) throws DuplicateException, Exception {
        Optional<Employee> existing = employeeDAO.findById(dto.getEmployeeId());
        if (existing.isPresent()) {
            throw new DuplicateException("Employee with ID " + dto.getEmployeeId() + " already exists.");
        }
        Employee entity = converter.getEmployee(dto);
        boolean saved = employeeDAO.save(entity);
        if (!saved) {
            throw new Exception("Failed to save employee.");
        }
    }

    @Override
    public void updateEmployee(Employeedto dto) throws NotFoundException, Exception {
        Optional<Employee> existing = employeeDAO.findById(dto.getEmployeeId());
        if (existing.isEmpty()) {
            throw new NotFoundException("Employee with ID " + dto.getEmployeeId() + " not found for update.");
        }
        Employee entity = converter.getEmployee(dto);
        boolean updated = employeeDAO.update(entity);
        if (!updated) {
            throw new Exception("Failed to update employee.");
        }
    }

    @Override
    public boolean deleteEmployee(String id) throws InUseException, NotFoundException, Exception {
        Optional<Employee> existing = employeeDAO.findById(id);
        if (existing.isEmpty()) {
            throw new NotFoundException("Employee with ID " + id + " not found for deletion.");
        }
        try {
            return employeeDAO.delete(id);
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new InUseException("Cannot delete employee with ID " + id + "; it is linked to other records (e.g., salaries, attendance).");
            }
            throw new Exception("Error deleting employee: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextId() throws SQLException {
        return employeeDAO.getNextId();
    }

    @Override
    public List<String> getAllEmployeeIds() throws SQLException {
        return employeeDAO.getAllIds();
    }

    @Override
    public int getEmployeeCount() throws Exception {
        return employeeDAO.getEmployeeCount();
    }

    @Override
    public Employeedto searchEmployee(String employeeId) throws SQLException, NotFoundException {
        Optional<Employee> employeeOptional = employeeDAO.findById(employeeId);
        if (employeeOptional.isPresent()) {
            return converter.getEmployeedto(employeeOptional.get());
        } else {
            throw new NotFoundException("Employee with ID " + employeeId + " not found.");
        }
    }
}