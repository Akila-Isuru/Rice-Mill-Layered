package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.custom.AttendanceDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Attendance;
import lk.ijse.gdse74.mytest2.responsive.utill.CrudUtill;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public String generateNextId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtill.execute("SELECT attendance_id FROM attendance ORDER BY attendance_id DESC LIMIT 1");
        char tableChar = 'A';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(tableChar + "%03d", nextIdNumber);
            return nextIdString;
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(Attendance entity) throws SQLException{
        return CrudUtill.execute(
                "INSERT INTO attendance (attendance_id, employee_id, date, status, in_time, out_time, hours_worked) VALUES (?,?,?,?,?,?,?)",
                entity.getAttendanceId(),
                entity.getEmployeeId(),
                Date.valueOf(entity.getDate()),
                entity.getStatus(),
                (entity.getInTime() != null) ? Time.valueOf(entity.getInTime()) : null,
                (entity.getOutTime() != null) ? Time.valueOf(entity.getOutTime()) : null,
                entity.getHoursWorked()
        );
    }

    @Override
    public boolean update(Attendance entity) throws SQLException{
        return CrudUtill.execute(
                "UPDATE attendance SET employee_id = ?, date = ?, status = ?, in_time = ?, out_time = ?, hours_worked = ? WHERE attendance_id = ?",
                entity.getEmployeeId(),
                Date.valueOf(entity.getDate()),
                entity.getStatus(),
                (entity.getInTime() != null) ? Time.valueOf(entity.getInTime()) : null,
                (entity.getOutTime() != null) ? Time.valueOf(entity.getOutTime()) : null,
                entity.getHoursWorked(),
                entity.getAttendanceId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException{
        String sql = "DELETE FROM attendance WHERE attendance_id=?";
        return CrudUtill.execute(sql, id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<Attendance> findById(String id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Attendance search(String id) throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtill.execute("SELECT * FROM attendance WHERE attendance_id = ?", id);
        if (rs.next()) {
            return new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("employee_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    (rs.getTime("in_time") != null) ? rs.getTime("in_time").toLocalTime() : null,
                    (rs.getTime("out_time") != null) ? rs.getTime("out_time").toLocalTime() : null,
                    rs.getDouble("hours_worked")
            );
        }
        return null;
    }

    @Override
    public List<Attendance> getAll() throws SQLException{
        ResultSet rs = CrudUtill.execute("SELECT * FROM attendance");
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        while (rs.next()) {
            attendanceList.add(new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("employee_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    (rs.getTime("in_time") != null) ? rs.getTime("in_time").toLocalTime() : null,
                    (rs.getTime("out_time") != null) ? rs.getTime("out_time").toLocalTime() : null,
                    rs.getDouble("hours_worked")
            ));
        }
        return attendanceList;
    }

    @Override
    public String getNextId() throws SQLException {
        return "";
    }

    @Override
    public ArrayList<Attendance> getAttendanceByEmployeeIdAndMonth(String employeeId, LocalDate monthStart, LocalDate monthEnd) throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtill.execute(
                "SELECT * FROM attendance WHERE employee_id = ? AND date BETWEEN ? AND ?",
                employeeId, Date.valueOf(monthStart), Date.valueOf(monthEnd)
        );
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        while (rs.next()) {
            attendanceList.add(new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("employee_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    (rs.getTime("in_time") != null) ? rs.getTime("in_time").toLocalTime() : null,
                    (rs.getTime("out_time") != null) ? rs.getTime("out_time").toLocalTime() : null,
                    rs.getDouble("hours_worked")
            ));
        }
        return attendanceList;
    }

    @Override
    public boolean isAttendanceRecordedForDate(String employeeId, LocalDate date) throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtill.execute(
                "SELECT COUNT(*) FROM attendance WHERE employee_id = ? AND date = ?",
                employeeId, Date.valueOf(date)
        );
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}