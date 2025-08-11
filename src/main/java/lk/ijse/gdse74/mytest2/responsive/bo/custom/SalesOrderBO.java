package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.dto.SalesOrderdto;

import java.sql.SQLException;
import java.util.List;

public interface SalesOrderBO extends SuperBO {
    boolean placeOrder(SalesOrderdto dto) throws SQLException;
    String getNextOrderId() throws SQLException;
    List<SalesOrderdto> getAllSalesOrders() throws SQLException;

}