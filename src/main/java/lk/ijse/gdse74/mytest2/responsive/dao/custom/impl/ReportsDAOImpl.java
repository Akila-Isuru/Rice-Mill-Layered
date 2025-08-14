package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.custom.ReportsDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.ReportsEntity;
import lk.ijse.gdse74.mytest2.responsive.utill.CrudUtill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportsDAOImpl implements ReportsDAO {

    @Override
    public boolean save(ReportsEntity entity) throws SQLException {
        return CrudUtill.execute(
                "insert into reports values (?,?,?)",
                entity.getReportId(),
                entity.getReportType(),
                entity.getReportDate()
        );
    }

    @Override
    public boolean update(ReportsEntity entity) throws SQLException{
        return CrudUtill.execute(
                "update reports set report_type =?,generated_date = ? where report_id =?",
                entity.getReportType(),
                entity.getReportDate(),
                entity.getReportId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException{
        String sql = "delete from reports where report_id=?";
        return CrudUtill.execute(sql, id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<ReportsEntity> findById(String id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public ReportsEntity search(String id) throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtill.execute("select * from reports where report_id = ?", id);
        if (rs.next()) {
            return new ReportsEntity(
                    rs.getString("report_id"),
                    rs.getString("report_type"),
                    rs.getString("generated_date")
            );
        }
        return null;
    }

    @Override
    public List<ReportsEntity> getAll() throws SQLException {
        ResultSet rs = CrudUtill.execute("select * from reports");
        ArrayList<ReportsEntity> reports = new ArrayList<>();
        while (rs.next()) {
            ReportsEntity reportsEntity = new ReportsEntity(
                    rs.getString("report_id"),
                    rs.getString("report_type"),
                    rs.getString("generated_date")
            );
            reports.add(reportsEntity);
        }
        return reports;
    }

    @Override
    public String getNextId() throws SQLException {
        return "";
    }

    // If you need a generateNextId, uncomment and implement based on your logic
    /*
    @Override
    public String generateNextId() throws SQLException, ClassNotFoundException {
        // Implement logic to generate the next report ID (e.g., RP001, RP002)
        ResultSet rst = CrudUtill.execute("SELECT report_id FROM reports ORDER BY report_id DESC LIMIT 1");
        if (rst.next()){
            String lastId = rst.getString(1);
            int num = Integer.parseInt(lastId.substring(2)) + 1;
            return String.format("RP%03d", num);
        }
        return "RP001";
    }
    */
}