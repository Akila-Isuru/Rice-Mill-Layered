package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.InventoryDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryDAOImpl implements InventoryDAO {

    @Override
    public List<Inventory> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM inventory");
        List<Inventory> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new Inventory(
                    resultSet.getString("inventory_id"),
                    resultSet.getString("product_id"),
                    resultSet.getInt("curent_stock_bags"),
                    resultSet.getDate("last_updated")
            ));
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT inventory_id FROM inventory ORDER BY inventory_id DESC LIMIT 1");
        char tableChar = 'I';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(tableChar + "%03d", nextIdNumber);
            return nextIdString;
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(Inventory inventory) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO inventory (inventory_id, product_id, curent_stock_bags, last_updated) VALUES (?, ?, ?, ?)",
                inventory.getInventoryId(),
                inventory.getProductId(),
                inventory.getCurrentStockBags(),
                inventory.getLastUpdated()
        );
    }

    @Override
    public boolean update(Inventory inventory) throws SQLException {
        return SQLUtill.execute(
                "UPDATE inventory SET product_id = ?, curent_stock_bags = ?, last_updated = ? WHERE inventory_id = ?",
                inventory.getProductId(),
                inventory.getCurrentStockBags(),
                inventory.getLastUpdated(),
                inventory.getInventoryId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM inventory WHERE inventory_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT inventory_id FROM inventory");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Inventory> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM inventory WHERE inventory_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Inventory(
                    resultSet.getString("inventory_id"),
                    resultSet.getString("product_id"),
                    resultSet.getInt("curent_stock_bags"),
                    resultSet.getDate("last_updated")
            ));
        }
        return Optional.empty();
    }
}