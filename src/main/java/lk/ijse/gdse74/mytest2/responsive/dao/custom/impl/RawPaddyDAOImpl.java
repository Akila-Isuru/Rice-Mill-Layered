package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.RawPaddyDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.RawPaddy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class RawPaddyDAOImpl implements RawPaddyDAO {

    @Override
    public List<RawPaddy> getAll() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM raw_paddy");
        List<RawPaddy> rawPaddies = new ArrayList<>();
        while (rs.next()) {
            rawPaddies.add(new RawPaddy(
                    rs.getString("paddy_id"),
                    rs.getString("supplier_id"),
                    rs.getString("farmer_id"),
                    rs.getBigDecimal("quantity_kg"),
                    rs.getBigDecimal("moisture_level"),
                    rs.getBigDecimal("purchase_price_per_kg"),
                    rs.getDate("purchase_date")
            ));
        }
        return rawPaddies;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT paddy_id FROM raw_paddy ORDER BY paddy_id DESC LIMIT 1");
        char tableChar = 'P'; // For Paddy ID
        if (rs.next()) {
            String lastId = rs.getString(1);
            String lastIdNumberString = lastId.replaceAll("[^\\d]", ""); // Extract only digits
            if (lastIdNumberString.isEmpty()) {
                return tableChar + "001"; // Fallback if no numbers found
            }
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
        return tableChar + "001";
    }

    @Override
    public boolean save(RawPaddy entity) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO raw_paddy (paddy_id, supplier_id, farmer_id, quantity_kg, moisture_level, purchase_price_per_kg, purchase_date) VALUES (?,?,?,?,?,?,?)",
                entity.getPaddyId(),
                entity.getSupplierId(),
                entity.getFarmerId(),
                entity.getQuantityKg(),
                entity.getMoistureLevel(),
                entity.getPurchasePricePerKg(),
                entity.getPurchaseDate()
        );
    }

    @Override
    public boolean update(RawPaddy entity) throws SQLException {
        return SQLUtill.execute(
                "UPDATE raw_paddy SET supplier_id=?, farmer_id=?, quantity_kg=?, moisture_level=?, purchase_price_per_kg=?, purchase_date=? WHERE paddy_id =?",
                entity.getSupplierId(),
                entity.getFarmerId(),
                entity.getQuantityKg(),
                entity.getMoistureLevel(),
                entity.getPurchasePricePerKg(),
                entity.getPurchaseDate(),
                entity.getPaddyId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM raw_paddy WHERE paddy_id=?";
        return SQLUtill.execute(sql, id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        ResultSet rs = SQLUtill.execute("SELECT paddy_id FROM raw_paddy");
        while(rs.next()){
            ids.add(rs.getString("paddy_id"));
        }
        return ids;
    }

    @Override
    public Optional<RawPaddy> findById(String id) throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM raw_paddy WHERE paddy_id = ?", id);
        if (rs.next()) {
            return Optional.of(new RawPaddy(
                    rs.getString("paddy_id"),
                    rs.getString("supplier_id"),
                    rs.getString("farmer_id"),
                    rs.getBigDecimal("quantity_kg"),
                    rs.getBigDecimal("moisture_level"),
                    rs.getBigDecimal("purchase_price_per_kg"),
                    rs.getDate("purchase_date")
            ));
        }
        return Optional.empty();
    }
}