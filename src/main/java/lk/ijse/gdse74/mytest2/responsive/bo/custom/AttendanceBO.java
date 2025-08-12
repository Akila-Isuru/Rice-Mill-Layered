package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.dto.AttendanceDto;
import lk.ijse.gdse74.mytest2.responsive.dto.Employeedto;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceBO extends SuperBO {
    String getNextAttendanceId() throws SQLException, ClassNotFoundException;
    boolean saveAttendance(AttendanceDto dto) throws SQLException, ClassNotFoundException;
    boolean updateAttendance(AttendanceDto dto) throws SQLException, ClassNotFoundException;
    boolean deleteAttendance(String id) throws SQLException, ClassNotFoundException;
    List<AttendanceDto> getAllAttendance() throws SQLException, ClassNotFoundException;
    List<AttendanceDto> getAttendanceByEmployeeIdAndMonth(String employeeId, LocalDate monthStart, LocalDate monthEnd) throws SQLException, ClassNotFoundException;
    boolean isAttendanceRecordedForDate(String employeeId, LocalDate date) throws SQLException, ClassNotFoundException;
    Employeedto getEmployeeById(String employeeId) throws SQLException, ClassNotFoundException;
    List<Employeedto> getAllEmployees() throws SQLException, ClassNotFoundException;
}