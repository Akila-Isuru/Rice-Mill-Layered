package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SalesOrderDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.SalesOrder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalesOrderDAOImpl implements SalesOrderDAO {

    @Override
    public List<SalesOrder> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM sales_order");
        List<SalesOrder> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new SalesOrder(
                    resultSet.getString("order_id"),
                    resultSet.getString("customer_id"),
                    resultSet.getDate("order_date"),
                    resultSet.getInt("total_amount")
            ));
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT order_id FROM sales_order ORDER BY order_id DESC LIMIT 1");
        char tableChar = 'O';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(SalesOrder salesOrder) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO sales_order (order_id, customer_id, order_date, total_amount) VALUES (?, ?, ?, ?)",
                salesOrder.getOrderId(),
                salesOrder.getCustomerId(),
                salesOrder.getOrderDate(),
                salesOrder.getOrderAmount()
        );
    }

    @Override
    public boolean update(SalesOrder salesOrder) throws SQLException {
        return SQLUtill.execute(
                "UPDATE sales_order SET customer_id = ?, order_date = ?, total_amount = ? WHERE order_id = ?",
                salesOrder.getCustomerId(),
                salesOrder.getOrderDate(),
                salesOrder.getOrderAmount(),
                salesOrder.getOrderId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM sales_order WHERE order_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT order_id FROM sales_order");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<SalesOrder> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM sales_order WHERE order_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new SalesOrder(
                    resultSet.getString("order_id"),
                    resultSet.getString("customer_id"),
                    resultSet.getDate("order_date"),
                    resultSet.getInt("total_amount")
            ));
        }
        return Optional.empty();
    }
}