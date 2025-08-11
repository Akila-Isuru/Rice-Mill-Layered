package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.FarmerBO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.FarmerDAO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer; // Import the new Farmer entity

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FarmerBOImpl implements FarmerBO {

    private final FarmerDAO farmerDAO = DAOFactory.getInstance().getDAO(DAOTypes.FARMER);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<FarmerDTO> getAllFarmers() throws SQLException {
        List<Farmer> farmers = farmerDAO.getAll();
        List<FarmerDTO> farmerDTOS = new ArrayList<>();
        for (Farmer farmer : farmers) {
            farmerDTOS.add(converter.getFarmerDTO(farmer));
        }
        return farmerDTOS;
    }

    @Override
    public void saveFarmer(FarmerDTO dto) throws DuplicateException, Exception {

        Optional<Farmer> optionalFarmer = farmerDAO.findById(dto.getFarmerId());
        if (optionalFarmer.isPresent()) {
            throw new DuplicateException("Duplicate farmer id");
        }


        Optional<Farmer> farmerByContactOptional = farmerDAO.findFarmerByContactNumber(dto.getContactNumber());
        if (farmerByContactOptional.isPresent()) {
            throw new DuplicateException("Duplicate farmer contact number");
        }

        Farmer farmer = converter.getFarmer(dto);
        farmerDAO.save(farmer);
    }

    @Override
    public void updateFarmer(FarmerDTO dto) throws SQLException, DuplicateException {
        Optional<Farmer> optionalFarmer = farmerDAO.findById(dto.getFarmerId());
        if (optionalFarmer.isEmpty()) {
            throw new NotFoundException("Farmer not found");
        }


        Optional<Farmer> farmerByContactOptional = farmerDAO.findFarmerByContactNumber(dto.getContactNumber());
        if (farmerByContactOptional.isPresent()) {
            Farmer farmer = farmerByContactOptional.get();
            if (!farmer.getFarmerId().equals(dto.getFarmerId())) {
                throw new DuplicateException("Duplicate contact number");
            }
        }

        Farmer farmer = converter.getFarmer(dto);
        farmerDAO.update(farmer);
    }

    @Override
    public boolean deleteFarmer(String id) throws InUseException, Exception {
        Optional<Farmer> optionalFarmer = farmerDAO.findById(id);
        if (optionalFarmer.isEmpty()) {
            throw new NotFoundException("Farmer not found..!");
        }

        try {
            return farmerDAO.delete(id);
        } catch (Exception e) {

            throw new InUseException("Cannot delete farmer, it is currently in use or associated with other data.");
        }
    }

    @Override
    public String getNextId() throws SQLException {
        return farmerDAO.getNextId();
    }

    @Override
    public List<String> getAllFarmerIds() throws SQLException {
        return farmerDAO.getAllIds();
    }
}