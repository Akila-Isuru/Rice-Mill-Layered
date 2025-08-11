package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.dto.Reportsdto;
import java.sql.SQLException;
import java.util.List;

public interface ReportsBO extends SuperBO {
    boolean saveReport(Reportsdto dto) throws SQLException, ClassNotFoundException;
    boolean updateReport(Reportsdto dto) throws SQLException, ClassNotFoundException;
    boolean deleteReport(String id) throws SQLException, ClassNotFoundException;
    Reportsdto searchReport(String id) throws SQLException, ClassNotFoundException;
    List<Reportsdto> getAllReports() throws SQLException, ClassNotFoundException;
}