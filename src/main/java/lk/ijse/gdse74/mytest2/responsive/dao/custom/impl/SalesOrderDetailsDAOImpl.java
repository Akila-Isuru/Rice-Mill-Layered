package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SalesOrderDetailsDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.SalesOrderDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalesOrderDetailsDAOImpl implements SalesOrderDetailsDAO {

    @Override
    public List<SalesOrderDetails> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM sales_order_details");
        List<SalesOrderDetails> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new SalesOrderDetails(
                    resultSet.getString("order_id"),
                    resultSet.getString("product_id"),
                    resultSet.getInt("unit_price"),
                    resultSet.getInt("qty"),
                    resultSet.getInt("total_price")
            ));
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        return null;
    }

    @Override
    public boolean save(SalesOrderDetails salesOrderDetails) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO sales_order_details VALUES (?, ?, ?, ?, ?)",
                salesOrderDetails.getOrderId(),
                salesOrderDetails.getProductId(),
                salesOrderDetails.getUnitPrice(),
                salesOrderDetails.getQty(),
                salesOrderDetails.getTotalPrice()
        );
    }

    @Override
    public boolean save(List<SalesOrderDetails> salesOrderDetailsList) throws SQLException {
        for (SalesOrderDetails detail : salesOrderDetailsList) {
            boolean isSaved = save(detail);
            if (!isSaved) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(SalesOrderDetails salesOrderDetails) throws SQLException {
        return SQLUtill.execute(
                "UPDATE sales_order_details SET unit_price = ?, qty = ?, total_price = ? WHERE order_id = ? AND product_id = ?",
                salesOrderDetails.getUnitPrice(),
                salesOrderDetails.getQty(),
                salesOrderDetails.getTotalPrice(),
                salesOrderDetails.getOrderId(),
                salesOrderDetails.getProductId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {

        return SQLUtill.execute("DELETE FROM sales_order_details WHERE order_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {

        ResultSet resultSet = SQLUtill.execute("SELECT order_id FROM sales_order_details");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<SalesOrderDetails> findById(String id) throws SQLException {

        ResultSet resultSet = SQLUtill.execute("SELECT * FROM sales_order_details WHERE order_id = ? LIMIT 1", id);
        if (resultSet.next()) {
            return Optional.of(new SalesOrderDetails(
                    resultSet.getString("order_id"),
                    resultSet.getString("product_id"),
                    resultSet.getInt("unit_price"),
                    resultSet.getInt("qty"),
                    resultSet.getInt("total_price")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<SalesOrderDetails> getDetailsByOrderId(String orderId) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM sales_order_details WHERE order_id = ?", orderId);
        List<SalesOrderDetails> detailsList = new ArrayList<>();
        while (resultSet.next()) {
            detailsList.add(new SalesOrderDetails(
                    resultSet.getString("order_id"),
                    resultSet.getString("product_id"),
                    resultSet.getInt("unit_price"),
                    resultSet.getInt("qty"),
                    resultSet.getInt("total_price")
            ));
        }
        return detailsList;
    }
}