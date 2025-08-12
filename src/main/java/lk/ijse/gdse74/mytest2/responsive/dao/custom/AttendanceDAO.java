package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface AttendanceDAO extends CrudDAO<Attendance,String> {
    Attendance search(String id) throws SQLException, ClassNotFoundException;

    ArrayList<Attendance> getAttendanceByEmployeeIdAndMonth(String employeeId, LocalDate monthStart, LocalDate monthEnd) throws SQLException, ClassNotFoundException;
    boolean isAttendanceRecordedForDate(String employeeId, LocalDate date) throws SQLException, ClassNotFoundException;
    String generateNextId() throws SQLException, ClassNotFoundException;
}