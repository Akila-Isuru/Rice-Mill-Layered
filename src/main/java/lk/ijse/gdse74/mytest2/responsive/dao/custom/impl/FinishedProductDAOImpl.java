package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.FinishedProductDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.FinishedProduct;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FinishedProductDAOImpl implements FinishedProductDAO {

    @Override
    public List<FinishedProduct> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM finished_product");
        List<FinishedProduct> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new FinishedProduct(
                    resultSet.getString("product_id"),
                    resultSet.getString("milling_id"),
                    resultSet.getString("product_type"),
                    resultSet.getBigDecimal("packaging_size_kg"),
                    resultSet.getInt("total_quantity_bags"),
                    resultSet.getInt("price_per_bag")
            ));
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT product_id FROM finished_product ORDER BY product_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString("product_id");
            int nextNum = Integer.parseInt(lastId.substring(2)) + 1;
            return String.format("FP%03d", nextNum);
        }
        return "FP001";
    }

    @Override
    public boolean save(FinishedProduct finishedProduct) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO finished_product (product_id, milling_id, product_type, packaging_size_kg, total_quantity_bags, price_per_bag) VALUES (?, ?, ?, ?, ?, ?)",
                finishedProduct.getProductId(),
                finishedProduct.getMillingId(),
                finishedProduct.getProductType(),
                finishedProduct.getPackagingSizeKg(),
                finishedProduct.getTotalQuantityBags(),
                finishedProduct.getPricePerBag()
        );
    }

    @Override
    public boolean update(FinishedProduct finishedProduct) throws SQLException {
        return SQLUtill.execute(
                "UPDATE finished_product SET milling_id = ?, product_type = ?, packaging_size_kg = ?, total_quantity_bags = ?, price_per_bag = ? WHERE product_id = ?",
                finishedProduct.getMillingId(),
                finishedProduct.getProductType(),
                finishedProduct.getPackagingSizeKg(),
                finishedProduct.getTotalQuantityBags(),
                finishedProduct.getPricePerBag(),
                finishedProduct.getProductId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM finished_product WHERE product_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT product_id FROM finished_product");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<FinishedProduct> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM finished_product WHERE product_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new FinishedProduct(
                    resultSet.getString("product_id"),
                    resultSet.getString("milling_id"),
                    resultSet.getString("product_type"),
                    resultSet.getBigDecimal("packaging_size_kg"),
                    resultSet.getInt("total_quantity_bags"),
                    resultSet.getInt("price_per_bag")
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean reduceQuantity(String productId, int quantityToReduce) throws SQLException {

        return SQLUtill.execute(
                "UPDATE finished_product SET total_quantity_bags = total_quantity_bags - ? WHERE product_id = ?",
                quantityToReduce,
                productId
        );
    }
}