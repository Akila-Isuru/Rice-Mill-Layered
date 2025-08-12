package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.WasteManagementBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.WasteManagementDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.WasteManagementdto;
import lk.ijse.gdse74.mytest2.responsive.entity.WasteManagement;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WasteManagementBOImpl implements WasteManagementBO {

    private final WasteManagementDAO wasteManagementDAO = DAOFactory.getInstance().getDAO(DAOTypes.WASTE_MANAGEMENT);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public String getNextWasteId() throws SQLException, ClassNotFoundException {
        return wasteManagementDAO.generateNextId();
    }

    @Override
    public void saveWasteManagement(WasteManagementdto dto) throws DuplicateException, Exception {
        Optional<WasteManagement> existing = wasteManagementDAO.findById(dto.getWasteId());
        if (existing.isPresent()) {
            throw new DuplicateException("Waste record with ID " + dto.getWasteId() + " already exists.");
        }
        WasteManagement entity = converter.getWasteManagement(dto);
        boolean saved = wasteManagementDAO.save(entity);
        if (!saved) {
            throw new Exception("Failed to save waste management record.");
        }
    }

    @Override
    public void updateWasteManagement(WasteManagementdto dto) throws NotFoundException, Exception {
        Optional<WasteManagement> existing = wasteManagementDAO.findById(dto.getWasteId());
        if (existing.isEmpty()) {
            throw new NotFoundException("Waste record with ID " + dto.getWasteId() + " not found for update.");
        }
        WasteManagement entity = converter.getWasteManagement(dto);
        boolean updated = wasteManagementDAO.update(entity);
        if (!updated) {
            throw new Exception("Failed to update waste management record.");
        }
    }

    @Override
    public boolean deleteWasteManagement(String id) throws NotFoundException, Exception {
        Optional<WasteManagement> existing = wasteManagementDAO.findById(id);
        if (existing.isEmpty()) {
            throw new NotFoundException("Waste record with ID " + id + " not found for deletion.");
        }
        return wasteManagementDAO.delete(id);
    }

    @Override
    public List<WasteManagementdto> getAllWasteManagement() throws SQLException, ClassNotFoundException {
        List<WasteManagement> entities = wasteManagementDAO.getAll();
        return entities.stream()
                .map(converter::getWasteManagementDto)
                .collect(Collectors.toList());
    }

    @Override
    public WasteManagementdto searchWasteManagement(String wasteId) throws SQLException, ClassNotFoundException, NotFoundException {
        Optional<WasteManagement> wasteOptional = wasteManagementDAO.findById(wasteId);
        if (wasteOptional.isPresent()) {
            return converter.getWasteManagementDto(wasteOptional.get());
        } else {
            throw new NotFoundException("Waste record with ID " + wasteId + " not found.");
        }
    }
}