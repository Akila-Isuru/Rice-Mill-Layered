//package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;
//
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.MillingProcessBO; // Corrected import
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
//import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
//import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
//import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
//import lk.ijse.gdse74.mytest2.responsive.dao.custom.MillingProcessDAO; // Corrected import
//import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto; // Using the existing DTO name
//import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess; // Import the new entity
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class MillingProcessBOImpl implements MillingProcessBO {
//
//    private final MillingProcessDAO millingProcessDAO = DAOFactory.getInstance().getDAO(DAOTypes.MILLING_PROCESS); // Use correct DAO type
//    private final EntityDTOConverter converter = new EntityDTOConverter();
//
//    @Override
//    public List<MillingProcessdto> getAllMillingProcesses() throws SQLException {
//        List<MillingProcess> processes = millingProcessDAO.getAll();
//        List<MillingProcessdto> dtoList = new ArrayList<>();
//        for (MillingProcess process : processes) {
//            dtoList.add(converter.getMillingProcessdto(process)); // Use converter
//        }
//        return dtoList;
//    }
//
//    @Override
//    public void saveMillingProcess(MillingProcessdto dto) throws DuplicateException, Exception {
//        // Check for duplicate ID
//        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(dto.getMillingId());
//        if (optionalProcess.isPresent()) {
//            throw new DuplicateException("Milling process with this ID already exists.");
//        }
//
//        MillingProcess entity = converter.getMillingProcess(dto); // Use converter
//        millingProcessDAO.save(entity);
//    }
//
//    @Override
//    public void updateMillingProcess(MillingProcessdto dto) throws SQLException {
//        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(dto.getMillingId());
//        if (optionalProcess.isEmpty()) {
//            throw new NotFoundException("Milling process not found for update.");
//        }
//        MillingProcess entity = converter.getMillingProcess(dto); // Use converter
//        millingProcessDAO.update(entity);
//    }
//
//    @Override
//    public boolean deleteMillingProcess(String id) throws InUseException, Exception {
//        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(id);
//        if (optionalProcess.isEmpty()) {
//            throw new NotFoundException("Milling process not found for deletion.");
//        }
//        try {
//            return millingProcessDAO.delete(id);
//        } catch (SQLException e) {
//            // Consider specific SQL error codes for foreign key constraints if applicable
//            throw new InUseException("Cannot delete milling process; it might be linked to other records.");
//        } catch (Exception e) {
//            throw new Exception("Error deleting milling process: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public String getNextMillingProcessId() throws SQLException {
//        return millingProcessDAO.getNextId();
//    }
//
//    @Override
//    public List<String> getAllPaddyIdsForMilling() throws SQLException {
//        // This method fetches paddy IDs from the DAO.
//        // If paddy IDs come from a separate 'paddy' table, you'd need a PaddyDAO and call it here.
//        // For now, it fetches from milling_process table as per original model.
//        return millingProcessDAO.getAllPaddyIds();
//    }
//
//    @Override
//    public boolean checkPaddyIdExistsInProcess(String paddyId) throws SQLException {
//        return millingProcessDAO.isPaddyIdExistsInProcess(paddyId);
//    }
//}