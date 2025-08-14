package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.RawPaddyBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.RawPaddyDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.RawPaddy;
import lk.ijse.gdse74.mytest2.responsive.dto.RawPaddydto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RawPaddyBOImpl implements RawPaddyBO {

    private final RawPaddyDAO rawPaddyDAO = DAOFactory.getInstance().getDAO(DAOTypes.RAW_PADDY);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<RawPaddydto> getAllRawPaddy() throws SQLException {
        List<RawPaddy> entities = rawPaddyDAO.getAll();
        List<RawPaddydto> dtoList = new ArrayList<>();
        for (RawPaddy entity : entities) {
            dtoList.add(converter.getRawPaddydto(entity));
        }
        return dtoList;
    }

    @Override
    public void saveRawPaddy(RawPaddydto dto) throws DuplicateException, Exception {
        Optional<RawPaddy> existing = rawPaddyDAO.findById(dto.getPaddyId());
        if (existing.isPresent()) {
            throw new DuplicateException("Raw Paddy record with ID " + dto.getPaddyId() + " already exists.");
        }
        RawPaddy entity = converter.getRawPaddy(dto);
        boolean saved = rawPaddyDAO.save(entity);
        if (!saved) {
            throw new Exception("Failed to save raw paddy record.");
        }
    }

    @Override
    public void updateRawPaddy(RawPaddydto dto) throws NotFoundException, Exception {
        Optional<RawPaddy> existing = rawPaddyDAO.findById(dto.getPaddyId());
        if (existing.isEmpty()) {
            throw new NotFoundException("Raw Paddy record with ID " + dto.getPaddyId() + " not found for update.");
        }
        RawPaddy entity = converter.getRawPaddy(dto);
        boolean updated = rawPaddyDAO.update(entity);
        if (!updated) {
            throw new Exception("Failed to update raw paddy record.");
        }
    }

    @Override
    public boolean deleteRawPaddy(String id) throws InUseException, NotFoundException, Exception {
        Optional<RawPaddy> existing = rawPaddyDAO.findById(id);
        if (existing.isEmpty()) {
            throw new NotFoundException("Raw Paddy record with ID " + id + " not found for deletion.");
        }
        try {
            return rawPaddyDAO.delete(id);
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new InUseException("Cannot delete raw paddy record with ID " + id + "; it is linked to other records (e.g., milling processes).");
            }
            throw new Exception("Error deleting raw paddy record: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextId() throws SQLException {
        return rawPaddyDAO.getNextId();
    }

    @Override
    public List<String> getAllRawPaddyIds() throws SQLException {
        return rawPaddyDAO.getAllIds();
    }
}