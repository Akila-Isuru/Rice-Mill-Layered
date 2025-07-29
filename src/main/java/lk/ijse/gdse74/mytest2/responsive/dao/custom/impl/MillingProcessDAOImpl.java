package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.MillingProcessDAO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess; // Import the new entity

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MillingProcessDAOImpl implements MillingProcessDAO {

    @Override
    public List<MillingProcess> getAll() throws SQLException {
        String sql = "SELECT * FROM milling_process";
        ResultSet rs = SQLUtill.execute(sql);

        List<MillingProcess> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new MillingProcess(
                    rs.getString("milling_id"),
                    rs.getString("paddy_id"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getDouble("milled_quantity"),
                    rs.getDouble("broken_rice"),
                    rs.getDouble("husk_kg"), // Use database column name
                    rs.getDouble("bran_kg")  // Use database column name
            ));
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        String sql = "SELECT milling_id FROM milling_process ORDER BY milling_id DESC LIMIT 1";
        ResultSet rs = SQLUtill.execute(sql);

        if (rs.next()) {
            String lastId = rs.getString(1);
            int lastNumber = Integer.parseInt(lastId.substring(1)); // Assuming format like M001
            return String.format("M%03d", lastNumber + 1);
        }
        return "M001";
    }

    @Override
    public boolean save(MillingProcess entity) throws SQLException {
        String sql = "INSERT INTO milling_process (milling_id, paddy_id, start_time, end_time, milled_quantity, broken_rice, husk_kg, bran_kg) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return SQLUtill.execute(
                sql,
                entity.getMillingId(),
                entity.getPaddyId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getMilledQuantity(),
                entity.getBrokenRice(),
                entity.getHusk_kg(), // Use entity field name matching DB
                entity.getBran_kg()  // Use entity field name matching DB
        );
    }

    @Override
    public boolean update(MillingProcess entity) throws SQLException {
        String sql = "UPDATE milling_process SET paddy_id=?, start_time=?, end_time=?, " +
                "milled_quantity=?, broken_rice=?, husk_kg=?, bran_kg=? " +
                "WHERE milling_id=?";
        return SQLUtill.execute(
                sql,
                entity.getPaddyId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getMilledQuantity(),
                entity.getBrokenRice(),
                entity.getHusk_kg(), // Use entity field name matching DB
                entity.getBran_kg(),  // Use entity field name matching DB
                entity.getMillingId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM milling_process WHERE milling_id=?";
        return SQLUtill.execute(sql, id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        String sql = "SELECT milling_id FROM milling_process ORDER BY milling_id";
        ResultSet rs = SQLUtill.execute(sql);

        List<String> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getString("milling_id"));
        }
        return ids;
    }

    @Override
    public Optional<MillingProcess> findById(String id) throws SQLException {
        String sql = "SELECT * FROM milling_process WHERE milling_id = ?";
        ResultSet rs = SQLUtill.execute(sql, id);

        if (rs.next()) {
            return Optional.of(new MillingProcess(
                    rs.getString("milling_id"),
                    rs.getString("paddy_id"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getDouble("milled_quantity"),
                    rs.getDouble("broken_rice"),
                    rs.getDouble("husk_kg"),
                    rs.getDouble("bran_kg")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllPaddyIds() throws SQLException {
        // This might need to get unique paddy IDs from a 'paddy' table,
        // or just existing ones in milling_process.
        // Assuming it's meant to get unique paddy IDs that exist in the paddy table
        // For now, I'll use the existing query which gets paddy_ids from milling_process.
        // If there's a separate `paddy` table and you want IDs from there, adjust this query.
        String sql = "SELECT DISTINCT paddy_id FROM milling_process"; // Using DISTINCT if needed
        ResultSet rs = SQLUtill.execute(sql);

        List<String> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getString("paddy_id"));
        }
        return ids;
    }

    @Override
    public boolean isPaddyIdExistsInProcess(String paddyId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM milling_process WHERE paddy_id = ?";
        ResultSet rs = SQLUtill.execute(sql, paddyId);
        return rs.next() && rs.getInt(1) > 0;
    }
}