package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.MillingProcessDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time; // For java.sql.Time
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MillingProcessDAOImpl implements MillingProcessDAO {

    @Override
    public List<MillingProcess> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM milling_process");
        List<MillingProcess> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new MillingProcess(
                    resultSet.getString("milling_id"),
                    resultSet.getString("paddy_id"),
                    resultSet.getTime("start_time"),
                    resultSet.getTime("end_time"),
                    resultSet.getBigDecimal("milled_quantity"),
                    resultSet.getBigDecimal("broken_rice"),
                    resultSet.getBigDecimal("husk_kg"),
                    resultSet.getBigDecimal("bran_kg")
            ));
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT milling_id FROM milling_process ORDER BY milling_id DESC LIMIT 1");
        char tableChar = 'M'; // Assuming 'M' for Milling Process
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(1); // "001"
            int lastIdNumber = Integer.parseInt(lastIdNumberString); // 1
            int nextIdNumber = lastIdNumber + 1; // 2
            return String.format(tableChar + "%03d", nextIdNumber); // "M002"
        }
        return tableChar + "001"; // First ID
    }

    @Override
    public boolean save(MillingProcess millingProcess) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO milling_process (milling_id, paddy_id, start_time, end_time, milled_quantity, broken_rice, husk_kg, bran_kg) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                millingProcess.getMillingId(),
                millingProcess.getPaddyId(),
                millingProcess.getStartTime(),
                millingProcess.getEndTime(),
                millingProcess.getMilledQuantity(),
                millingProcess.getBrokenRice(),
                millingProcess.getHuskKg(),
                millingProcess.getBranKg()
        );
    }

    @Override
    public boolean update(MillingProcess millingProcess) throws SQLException {
        return SQLUtill.execute(
                "UPDATE milling_process SET paddy_id = ?, start_time = ?, end_time = ?, milled_quantity = ?, broken_rice = ?, husk_kg = ?, bran_kg = ? WHERE milling_id = ?",
                millingProcess.getPaddyId(),
                millingProcess.getStartTime(),
                millingProcess.getEndTime(),
                millingProcess.getMilledQuantity(),
                millingProcess.getBrokenRice(),
                millingProcess.getHuskKg(),
                millingProcess.getBranKg(),
                millingProcess.getMillingId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM milling_process WHERE milling_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT milling_id FROM milling_process");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<MillingProcess> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM milling_process WHERE milling_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new MillingProcess(
                    resultSet.getString("milling_id"),
                    resultSet.getString("paddy_id"),
                    resultSet.getTime("start_time"),
                    resultSet.getTime("end_time"),
                    resultSet.getBigDecimal("milled_quantity"),
                    resultSet.getBigDecimal("broken_rice"),
                    resultSet.getBigDecimal("husk_kg"),
                    resultSet.getBigDecimal("bran_kg")
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsMillingProcessByPaddyId(String paddyId) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT milling_id FROM milling_process WHERE paddy_id = ?", paddyId);
        return resultSet.next();
    }

    @Override
    public Optional<MillingProcess> findByPaddyId(String paddyId) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM milling_process WHERE paddy_id = ?", paddyId);
        if (resultSet.next()) {
            return Optional.of(new MillingProcess(
                    resultSet.getString("milling_id"),
                    resultSet.getString("paddy_id"),
                    resultSet.getTime("start_time"),
                    resultSet.getTime("end_time"),
                    resultSet.getBigDecimal("milled_quantity"),
                    resultSet.getBigDecimal("broken_rice"),
                    resultSet.getBigDecimal("husk_kg"),
                    resultSet.getBigDecimal("bran_kg")
            ));
        }
        return Optional.empty();
    }
}