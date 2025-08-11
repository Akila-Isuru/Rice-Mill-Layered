package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.SalesOrderBO;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.FinishedProductDAO;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SalesOrderDAO;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SalesOrderDetailsDAO;
import lk.ijse.gdse74.mytest2.responsive.db.DBConnection;
import lk.ijse.gdse74.mytest2.responsive.dto.SalesOrderDetailsdto;
import lk.ijse.gdse74.mytest2.responsive.dto.SalesOrderdto;
import lk.ijse.gdse74.mytest2.responsive.entity.SalesOrder;
import lk.ijse.gdse74.mytest2.responsive.entity.SalesOrderDetails;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesOrderBOImpl implements SalesOrderBO {

    private final SalesOrderDAO salesOrderDAO = DAOFactory.getInstance().getDAO(DAOTypes.SALES_ORDER);
    private final SalesOrderDetailsDAO salesOrderDetailsDAO = DAOFactory.getInstance().getDAO(DAOTypes.SALES_ORDER_DETAILS);
    private final FinishedProductDAO finishedProductDAO = DAOFactory.getInstance().getDAO(DAOTypes.FINISHED_PRODUCT);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public boolean placeOrder(SalesOrderdto dto) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);


            SalesOrder salesOrder = new SalesOrder(
                    dto.getOrderId(),
                    dto.getCustomerId(),
                    dto.getOrderDate(),
                    dto.getOrderAmount()
            );
            boolean orderSaved = salesOrderDAO.save(salesOrder);
            if (!orderSaved) {
                connection.rollback();
                return false;
            }


            List<SalesOrderDetails> detailsList = new ArrayList<>();
            for (SalesOrderDetailsdto detailDto : dto.getCartList()) {
                SalesOrderDetails detail = converter.getSalesOrderDetails(detailDto);
                detailsList.add(detail);


                boolean quantityReduced = finishedProductDAO.reduceQuantity(detail.getProductId(), detail.getUnitPrice());
                if (!quantityReduced) {
                    connection.rollback();
                    return false;
                }
            }

            boolean detailsSaved = salesOrderDetailsDAO.save(detailsList);
            if (!detailsSaved) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new SQLException("Failed to place order: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public String getNextOrderId() throws SQLException {
        return salesOrderDAO.getNextId();
    }

    @Override
    public List<SalesOrderdto> getAllSalesOrders() throws SQLException {
        List<SalesOrder> salesOrders = salesOrderDAO.getAll();
        List<SalesOrderdto> salesOrderdtos = new ArrayList<>();
        for (SalesOrder salesOrder : salesOrders) {
            salesOrderdtos.add(converter.getSalesOrderdto(salesOrder));
        }
        return salesOrderdtos;
    }
}