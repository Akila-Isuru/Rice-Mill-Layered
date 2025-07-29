package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.MachineMaintenanceDAO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance; // Import the new entity

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MachineMaintenanceDAOImpl implements MachineMaintenanceDAO {

    @Override
    public List<MachineMaintenance> getAll() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM machine_maintenance");
        List<MachineMaintenance> maintenances = new ArrayList<>();
        while (rs.next()) {
            MachineMaintenance machineMaintenance = new MachineMaintenance(
                    rs.getString("maintenance_id"),
                    rs.getString("machine_name"),
                    rs.getString("maintenance_date"),
                    rs.getString("description"),
                    rs.getInt("cost")
            );
            maintenances.add(machineMaintenance);
        }
        return maintenances;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT maintenance_id FROM machine_maintenance ORDER BY maintenance_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            try {
                // Assuming ID format like "MM001"
                int lastIdNumber = Integer.parseInt(lastId.substring(2)); // Skip 'MM' and parse to int
                return String.format("MM%03d", lastIdNumber + 1); // Increment and format
            } catch (NumberFormatException e) {
                // If parsing fails (e.g., first ID is not MM001 or format changes)
                return "MM001";
            }
        }
        return "MM001"; // First ID
    }

    @Override
    public boolean save(MachineMaintenance machineMaintenance) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO machine_maintenance (maintenance_id, machine_name, maintenance_date, description, cost) VALUES (?,?,?,?,?)",
                machineMaintenance.getMaintenanceId(),
                machineMaintenance.getMachineName(),
                machineMaintenance.getMaintenanceDate(),
                machineMaintenance.getDescription(),
                machineMaintenance.getCost()
        );
    }

    @Override
    public boolean update(MachineMaintenance machineMaintenance) throws SQLException {
        return SQLUtill.execute(
                "UPDATE machine_maintenance SET machine_name=?, maintenance_date=?, description=?, cost=? WHERE maintenance_id=?",
                machineMaintenance.getMachineName(),
                machineMaintenance.getMaintenanceDate(),
                machineMaintenance.getDescription(),
                machineMaintenance.getCost(),
                machineMaintenance.getMaintenanceId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM machine_maintenance WHERE maintenance_id=?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT maintenance_id FROM machine_maintenance");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<MachineMaintenance> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM machine_maintenance WHERE maintenance_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new MachineMaintenance(
                    resultSet.getString("maintenance_id"),
                    resultSet.getString("machine_name"),
                    resultSet.getString("maintenance_date"),
                    resultSet.getString("description"),
                    resultSet.getInt("cost")
            ));
        }
        return Optional.empty();
    }
}