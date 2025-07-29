//package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;
//
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.MachineMaintenanceBO; // Corrected import
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
//import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
//import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
//import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
//import lk.ijse.gdse74.mytest2.responsive.dao.custom.MachineMaintenanceDAO; // Corrected import
//import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto; // Using the existing DTO name
//import lk.ijse.gdse74.mytest2.responsive.entity.MachineMaintenance; // Import the new entity
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class MachineMaintenanceBOImpl implements MachineMaintenanceBO {
//
//    private final MachineMaintenanceDAO machineMaintenanceDAO = DAOFactory.getInstance().getDAO(DAOTypes.MACHINE_MAINTENANCE); // Use correct DAO type
//    private final EntityDTOConverter converter = new EntityDTOConverter();
//
//    @Override
//    public List<MachineMaintenancedto> getAllMachineMaintenance() throws SQLException {
//        List<MachineMaintenance> maintenances = machineMaintenanceDAO.getAll();
//        List<MachineMaintenancedto> maintenanceDTOS = new ArrayList<>();
//        for (MachineMaintenance maintenance : maintenances) {
//            maintenanceDTOS.add(converter.getMachineMaintenancedto(maintenance)); // Use converter
//        }
//        return maintenanceDTOS;
//    }
//
//    @Override
//    public void saveMachineMaintenance(MachineMaintenancedto dto) throws DuplicateException, Exception {
//        // Check for duplicate ID
//        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(dto.getMaintenanceId());
//        if (optionalMaintenance.isPresent()) {
//            throw new DuplicateException("Duplicate Maintenance ID");
//        }
//
//        MachineMaintenance maintenance = converter.getMachineMaintenance(dto); // Use converter
//        machineMaintenanceDAO.save(maintenance);
//    }
//
//    @Override
//    public void updateMachineMaintenance(MachineMaintenancedto dto) throws SQLException {
//        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(dto.getMaintenanceId());
//        if (optionalMaintenance.isEmpty()) {
//            throw new NotFoundException("Maintenance record not found");
//        }
//        MachineMaintenance maintenance = converter.getMachineMaintenance(dto); // Use converter
//        machineMaintenanceDAO.update(maintenance);
//    }
//
//    @Override
//    public boolean deleteMachineMaintenance(String id) throws InUseException, Exception {
//        Optional<MachineMaintenance> optionalMaintenance = machineMaintenanceDAO.findById(id);
//        if (optionalMaintenance.isEmpty()) {
//            throw new NotFoundException("Maintenance record not found..!");
//        }
//        try {
//            return machineMaintenanceDAO.delete(id);
//        } catch (SQLException e) {
//            // Here, you might check SQL error codes for foreign key constraints
//            throw new InUseException("Cannot delete maintenance record, it may be associated with other data.");
//        } catch (Exception e) {
//            // Generic catch for other exceptions during delete
//            throw new Exception("Error deleting maintenance record: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public String getNextId() throws SQLException {
//        return machineMaintenanceDAO.getNextId();
//    }
//}