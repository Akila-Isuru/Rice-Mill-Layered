package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.MillingProcessDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal; // Import for BigDecimal

public class MillingProcessDAOImpl implements MillingProcessDAO {

    @Override
    public List<MillingProcess> getAll() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM milling_process");
        List<MillingProcess> processes = new ArrayList<>();
        while (rs.next()) {
            processes.add(new MillingProcess(
                    rs.getString("milling_id"),
                    rs.getString("paddy_id"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("milled_quantity"),
                    rs.getBigDecimal("broken_rice"),
                    rs.getBigDecimal("husk_kg"), // Use DB column name for ResultSet
                    rs.getBigDecimal("bran_kg")  // Use DB column name for ResultSet
            ));
        }
        return processes;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT milling_id FROM milling_process ORDER BY milling_id DESC LIMIT 1");
        char tableChar = 'M'; // For Milling ID
        if (rs.next()) {
            String lastId = rs.getString(1);
            // Extract numbers from the ID (e.g., M001 -> 1)
            String lastIdNumberString = lastId.replaceAll("[^\\d]", "");
            if (lastIdNumberString.isEmpty()) {
                return tableChar + "001"; // Fallback if no numbers found
            }
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
        return tableChar + "001"; // Initial ID if no records exist
    }

    @Override
    public boolean save(MillingProcess entity) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO milling_process (milling_id, paddy_id, start_time, end_time, milled_quantity, broken_rice, husk_kg, bran_kg) VALUES (?,?,?,?,?,?,?,?)",
                entity.getMillingId(),
                entity.getPaddyId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getMilledQuantity(),
                entity.getBrokenRice(),
                entity.getHuskKg(), // Use Entity's getter for 'husk_kg'
                entity.getBranKg()  // Use Entity's getter for 'bran_kg'
        );
    }

    @Override
    public boolean update(MillingProcess entity) throws SQLException {
        return SQLUtill.execute(
                "UPDATE milling_process SET paddy_id=?, start_time=?, end_time=?, milled_quantity=?, broken_rice=?, husk_kg=?, bran_kg=? WHERE milling_id =?",
                entity.getPaddyId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getMilledQuantity(),
                entity.getBrokenRice(),
                entity.getHuskKg(), // Use Entity's getter for 'husk_kg'
                entity.getBranKg(),  // Use Entity's getter for 'bran_kg'
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
        List<String> ids = new ArrayList<>();
        ResultSet rs = SQLUtill.execute("SELECT milling_id FROM milling_process");
        while(rs.next()){
            ids.add(rs.getString("milling_id"));
        }
        return ids;
    }

    @Override
    public Optional<MillingProcess> findById(String id) throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM milling_process WHERE milling_id = ?", id);
        if (rs.next()) {
            return Optional.of(new MillingProcess(
                    rs.getString("milling_id"),
                    rs.getString("paddy_id"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("milled_quantity"),
                    rs.getBigDecimal("broken_rice"),
                    rs.getBigDecimal("husk_kg"),
                    rs.getBigDecimal("bran_kg")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllPaddyIds() throws SQLException {
        // This method fetches paddy IDs from the 'raw_paddy' table,
        // as these are the raw materials available for milling.
        List<String> paddyIds = new ArrayList<>();
        // Assuming 'raw_paddy' table has a 'paddy_id' column
        ResultSet rs = SQLUtill.execute("SELECT paddy_id FROM raw_paddy");
        while (rs.next()) {
            paddyIds.add(rs.getString("paddy_id"));
        }
        return paddyIds;
    }

    @Override
    public boolean isPaddyIdExistsInProcess(String paddyId) throws SQLException {
        // This checks if a specific paddy_id is *already recorded* in the milling_process table.
        // This is useful to prevent a single batch of paddy from being milled multiple times.
        ResultSet rs = SQLUtill.execute("SELECT COUNT(*) FROM milling_process WHERE paddy_id = ?", paddyId);
        return rs.next() && rs.getInt(1) > 0;
    }
}