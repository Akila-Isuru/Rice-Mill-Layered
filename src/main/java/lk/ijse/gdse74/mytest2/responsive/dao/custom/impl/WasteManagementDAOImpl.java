package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.WasteManagementDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.WasteManagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date; // Explicitly import java.util.Date
import java.util.List;
import java.util.Optional;

public class WasteManagementDAOImpl implements WasteManagementDAO {

    @Override
    public List<WasteManagement> getAll() throws SQLException{
        ResultSet rs = SQLUtill.execute("SELECT * FROM waste_management");
        List<WasteManagement> wasteManagementList = new ArrayList<>();
        while (rs.next()) {
            java.sql.Date sqlDate = rs.getDate("recorded_date");
            Date utilDate = (sqlDate != null) ? new Date(sqlDate.getTime()) : null;

            wasteManagementList.add(new WasteManagement(
                    rs.getString("waste_id"),
                    rs.getString("milling_id"),
                    rs.getString("waste_type"),
                    rs.getInt("quantity"),
                    rs.getString("disposal_method"),
                    utilDate // Use java.util.Date
            ));
        }
        return wasteManagementList;
    }

    @Override
    public String getNextId() throws SQLException {
        return "";
    }

    @Override
    public String generateNextId() throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtill.execute("SELECT waste_id FROM waste_management ORDER BY waste_id DESC LIMIT 1");
        if (rs.next()) {
            String lastId = rs.getString("waste_id");
            int num = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("W%03d", num);
        }
        return "W001";
    }

    @Override
    public boolean save(WasteManagement entity) throws SQLException{
        java.sql.Date sqlDate = (entity.getRecordDate() != null) ? new java.sql.Date(entity.getRecordDate().getTime()) : null;

        return SQLUtill.execute(
                "INSERT INTO waste_management (waste_id, milling_id, waste_type, quantity, disposal_method, recorded_date) VALUES (?,?,?,?,?,?)",
                entity.getWasteId(),
                entity.getMillingId(),
                entity.getWasteType(),
                entity.getQuantity(),
                entity.getDisposalMethod(),
                sqlDate
        );
    }

    @Override
    public boolean update(WasteManagement entity) throws SQLException {
        java.sql.Date sqlDate = (entity.getRecordDate() != null) ? new java.sql.Date(entity.getRecordDate().getTime()) : null;

        return SQLUtill.execute(
                "UPDATE waste_management SET milling_id=?, waste_type=?, quantity=?, disposal_method=?, recorded_date=? WHERE waste_id=?",
                entity.getMillingId(),
                entity.getWasteType(),
                entity.getQuantity(),
                entity.getDisposalMethod(),
                sqlDate,
                entity.getWasteId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException{
        String sql = "DELETE FROM waste_management WHERE waste_id=?";
        return SQLUtill.execute(sql, id);
    }

    @Override
    public Optional<WasteManagement> findById(String id) throws SQLException{
        ResultSet rs = SQLUtill.execute("SELECT * FROM waste_management WHERE waste_id = ?", id);
        if (rs.next()) {
            java.sql.Date sqlDate = rs.getDate("recorded_date");
            Date utilDate = (sqlDate != null) ? new Date(sqlDate.getTime()) : null;

            return Optional.of(new WasteManagement(
                    rs.getString("waste_id"),
                    rs.getString("milling_id"),
                    rs.getString("waste_type"),
                    rs.getInt("quantity"),
                    rs.getString("disposal_method"),
                    utilDate // Use java.util.Date
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllIds() throws SQLException{
        List<String> ids = new ArrayList<>();
        ResultSet rs = SQLUtill.execute("SELECT waste_id FROM waste_management");
        while(rs.next()){
            ids.add(rs.getString("waste_id"));
        }
        return ids;
    }
}