package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.FinishedProductBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.FinishedProductDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.FinishedProductdto;
import lk.ijse.gdse74.mytest2.responsive.entity.FinishedProduct;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FinishedProductBOImpl implements FinishedProductBO {

    private final FinishedProductDAO finishedProductDAO = DAOFactory.getInstance().getDAO(DAOTypes.FINISHED_PRODUCT);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<FinishedProductdto> getAllFinishedProducts() throws SQLException {
        List<FinishedProduct> products = finishedProductDAO.getAll();
        List<FinishedProductdto> dtos = new ArrayList<>();
        for (FinishedProduct product : products) {
            dtos.add(converter.getFinishedProductdto(product));
        }
        return dtos;
    }

    @Override
    public void saveFinishedProduct(FinishedProductdto dto) throws DuplicateException, SQLException {
        Optional<FinishedProduct> optionalProduct = finishedProductDAO.findById(dto.getProductId());
        if (optionalProduct.isPresent()) {
            throw new DuplicateException("Duplicate Finished Product ID: " + dto.getProductId());
        }
        FinishedProduct entity = converter.getFinishedProduct(dto);
        boolean saved = finishedProductDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save finished product.");
        }
    }

    @Override
    public void updateFinishedProduct(FinishedProductdto dto) throws SQLException {
        Optional<FinishedProduct> optionalProduct = finishedProductDAO.findById(dto.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new NotFoundException("Finished Product not found with ID: " + dto.getProductId());
        }
        FinishedProduct entity = converter.getFinishedProduct(dto);
        boolean updated = finishedProductDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update finished product.");
        }
    }

    @Override
    public boolean deleteFinishedProduct(String id) throws InUseException, SQLException {
        Optional<FinishedProduct> optionalProduct = finishedProductDAO.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new NotFoundException("Finished Product not found with ID: " + id);
        }
        try {
            return finishedProductDAO.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("Cannot delete or update a parent row: a foreign key constraint fails")) {
                throw new InUseException("Finished Product ID: " + id + " is in use (e.g., in sales orders) and cannot be deleted.");
            }
            throw new SQLException("Error deleting finished product: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextFinishedProductId() throws SQLException {
        return finishedProductDAO.getNextId();
    }

    @Override
    public FinishedProductdto findFinishedProductById(String id) throws SQLException {
        Optional<FinishedProduct> optionalProduct = finishedProductDAO.findById(id);
        return optionalProduct.map(converter::getFinishedProductdto).orElse(null);
    }

    @Override
    public List<String> getAllFinishedProductIds() throws SQLException {
        return finishedProductDAO.getAllIds();
    }

    @Override
    public void reduceFinishedProductQuantity(String productId, int quantityToReduce) throws SQLException {
        Optional<FinishedProduct> optionalProduct = finishedProductDAO.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new NotFoundException("Finished Product not found with ID: " + productId);
        }
        FinishedProduct product = optionalProduct.get();
        if (product.getTotalQuantityBags() < quantityToReduce) {
            throw new SQLException("Insufficient stock for product ID: " + productId + ". Available: " + product.getTotalQuantityBags() + ", Requested: " + quantityToReduce);
        }
        boolean updated = finishedProductDAO.reduceQuantity(productId, quantityToReduce);
        if (!updated) {
            throw new SQLException("Failed to reduce quantity for product ID: " + productId);
        }
    }
}