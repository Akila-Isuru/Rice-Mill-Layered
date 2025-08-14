package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.MachineMaintenanceBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.MachineMaintenanceDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto;
import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MachineMaintenanceBOImpl implements MachineMaintenanceBO {

    private final MachineMaintenanceDAO machineMaintenanceDAO = DAOFactory.getInstance().getDAO(DAOTypes.MACHINE_MAINTENANCE);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<MachineMaintenancedto> getAllMachineMaintenance() throws SQLException {
        List<MachineMaintenance> maintenanceList = machineMaintenanceDAO.getAll();
        List<MachineMaintenancedto> dtos = new ArrayList<>();
        for (MachineMaintenance maintenance : maintenanceList) {
            dtos.add(converter.getMachineMaintenancedto(maintenance));
        }
        return dtos;
    }

    @Override
    public void saveMachineMaintenance(MachineMaintenancedto dto) throws DuplicateException, SQLException {
        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(dto.getMaintenanceId());
        if (optionalMaintenance.isPresent()) {
            throw new DuplicateException("Maintenance ID " + dto.getMaintenanceId() + " already exists.");
        }
        MachineMaintenance entity = converter.getMachineMaintenance(dto);
        boolean saved = machineMaintenanceDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save machine maintenance record.");
        }
    }

    @Override
    public void updateMachineMaintenance(MachineMaintenancedto dto) throws NotFoundException, SQLException {
        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(dto.getMaintenanceId());
        if (optionalMaintenance.isEmpty()) {
            throw new NotFoundException("Maintenance ID " + dto.getMaintenanceId() + " not found.");
        }
        MachineMaintenance entity = converter.getMachineMaintenance(dto);
        boolean updated = machineMaintenanceDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update machine maintenance record.");
        }
    }

    @Override
    public boolean deleteMachineMaintenance(String id) throws NotFoundException, InUseException, SQLException {
        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(id);
        if (optionalMaintenance.isEmpty()) {
            throw new NotFoundException("Maintenance ID " + id + " not found.");
        }
        try {
            return machineMaintenanceDAO.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Machine Maintenance ID " + id + " is linked to other records and cannot be deleted.");
            }
            throw new SQLException("Error deleting machine maintenance record: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextMaintenanceId() throws SQLException {
        return machineMaintenanceDAO.getNextId();
    }

    @Override
    public MachineMaintenancedto findMachineMaintenanceById(String id) throws SQLException {
        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(id);
        return optionalMaintenance.map(converter::getMachineMaintenancedto).orElse(null);
    }
}