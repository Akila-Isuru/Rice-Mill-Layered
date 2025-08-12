package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.AttendanceBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.EmployeeBO;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.AttendanceDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.AttendanceDto;
import lk.ijse.gdse74.mytest2.responsive.dto.Employeedto;
import lk.ijse.gdse74.mytest2.responsive.entity.Attendance;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttendanceBOImpl implements AttendanceBO {

    private final AttendanceDAO attendanceDAO = DAOFactory.getInstance().getDAO(DAOTypes.ATTENDANCE);

    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE);

    private final EntityDTOConverter converter = new EntityDTOConverter();


    @Override
    public String getNextAttendanceId() throws SQLException, ClassNotFoundException {
        return attendanceDAO.generateNextId();
    }

    @Override
    public boolean saveAttendance(AttendanceDto dto) throws SQLException, ClassNotFoundException {
        if (dto.getInTime() != null && dto.getOutTime() != null && dto.getHoursWorked() == 0) {
            long hours = Duration.between(dto.getInTime(), dto.getOutTime()).toHours();
            dto.setHoursWorked((double) hours);
        }
        return attendanceDAO.save(converter.getAttendance(dto));
    }

    @Override
    public boolean updateAttendance(AttendanceDto dto) throws SQLException, ClassNotFoundException {
        if (dto.getInTime() != null && dto.getOutTime() != null) {
            long hours = Duration.between(dto.getInTime(), dto.getOutTime()).toHours();
            dto.setHoursWorked((double) hours);
        }
        return attendanceDAO.update(converter.getAttendance(dto));
    }

    @Override
    public boolean deleteAttendance(String id) throws SQLException, ClassNotFoundException {
        return attendanceDAO.delete(id);
    }

    @Override
    public List<AttendanceDto> getAllAttendance() throws SQLException, ClassNotFoundException {
        List<Attendance> entities = attendanceDAO.getAll();
        return entities.stream()
                .map(converter::getAttendanceDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> getAttendanceByEmployeeIdAndMonth(String employeeId, LocalDate monthStart, LocalDate monthEnd) throws SQLException, ClassNotFoundException {
        List<Attendance> entities = attendanceDAO.getAttendanceByEmployeeIdAndMonth(employeeId, monthStart, monthEnd);
        return entities.stream()
                .map(converter::getAttendanceDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAttendanceRecordedForDate(String employeeId, LocalDate date) throws SQLException, ClassNotFoundException {
        return attendanceDAO.isAttendanceRecordedForDate(employeeId, date);
    }

    @Override
    public Employeedto getEmployeeById(String employeeId) throws SQLException, ClassNotFoundException {

        try {
            return employeeBO.searchEmployee(employeeId);
        } catch (lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException e) {

            throw new SQLException("Employee not found: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employeedto> getAllEmployees() throws SQLException, ClassNotFoundException {

        return employeeBO.getAllEmployees();
    }
}