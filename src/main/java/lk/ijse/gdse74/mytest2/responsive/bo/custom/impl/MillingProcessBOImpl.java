package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.MillingProcessBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.MillingProcessDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto;
import lk.ijse.gdse74.mytest2.responsive.entity.MillingProcess;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MillingProcessBOImpl implements MillingProcessBO {

    private final MillingProcessDAO millingProcessDAO = DAOFactory.getInstance().getDAO(DAOTypes.MILLING_PROCESS);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<MillingProcessdto> getAllMillingProcesses() throws SQLException {
        List<MillingProcess> processes = millingProcessDAO.getAll();
        return processes.stream()
                .map(converter::getMillingProcessDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveMillingProcess(MillingProcessdto dto) throws DuplicateException, SQLException {
        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(dto.getMillingId());
        if (optionalProcess.isPresent()) {
            throw new DuplicateException("Duplicate Milling Process ID: " + dto.getMillingId());
        }

        if (millingProcessDAO.existsMillingProcessByPaddyId(dto.getPaddyId())) {
            throw new DuplicateException("Paddy ID: " + dto.getPaddyId() + " is already used in another milling process.");
        }

        MillingProcess entity = converter.getMillingProcess(dto);
        boolean saved = millingProcessDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save milling process.");
        }
    }

    @Override
    public void updateMillingProcess(MillingProcessdto dto) throws SQLException, NotFoundException { // Added NotFoundException
        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(dto.getMillingId());
        if (optionalProcess.isEmpty()) {
            throw new NotFoundException("Milling Process not found with ID: " + dto.getMillingId());
        }

        Optional<MillingProcess> processByPaddyId = millingProcessDAO.findByPaddyId(dto.getPaddyId());
        if (processByPaddyId.isPresent() && !processByPaddyId.get().getMillingId().equals(dto.getMillingId())) {
            throw new DuplicateException("Paddy ID: " + dto.getPaddyId() + " is already used in another milling process.");
        }

        MillingProcess entity = converter.getMillingProcess(dto);
        boolean updated = millingProcessDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update milling process.");
        }
    }

    @Override
    public boolean deleteMillingProcess(String id) throws InUseException, SQLException, NotFoundException { // Added NotFoundException
        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(id);
        if (optionalProcess.isEmpty()) {
            throw new NotFoundException("Milling Process not found with ID: " + id);
        }

        try {
            return millingProcessDAO.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Milling Process ID: " + id + " is in use and cannot be deleted.");
            }
            throw new SQLException("Error deleting milling process: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextMillingProcessId() throws SQLException {
        return millingProcessDAO.getNextId();
    }

    @Override
    public boolean checkPaddyIdExistsInProcess(String paddyId) throws SQLException {
        return millingProcessDAO.existsMillingProcessByPaddyId(paddyId);
    }

    @Override
    public MillingProcessdto getMillingProcessByMillingId(String millingId) throws SQLException, ClassNotFoundException, NotFoundException {
        Optional<MillingProcess> optionalProcess = millingProcessDAO.findById(millingId);
        if (optionalProcess.isPresent()) {
            return converter.getMillingProcessDto(optionalProcess.get());
        } else {
            throw new NotFoundException("Milling Process with ID " + millingId + " not found.");
        }
    }

    @Override
    public List<String> getAllMillingProcessIds() throws SQLException, ClassNotFoundException {
        return millingProcessDAO.getAllIds();
    }
}