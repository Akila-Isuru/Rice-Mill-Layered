package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.SalesOrderDetails;

import java.sql.SQLException;
import java.util.List;

public interface SalesOrderDetailsDAO extends CrudDAO<SalesOrderDetails, String> {
    boolean save(List<SalesOrderDetails> salesOrderDetailsList) throws SQLException;
    List<SalesOrderDetails> getDetailsByOrderId(String orderId) throws SQLException;
}