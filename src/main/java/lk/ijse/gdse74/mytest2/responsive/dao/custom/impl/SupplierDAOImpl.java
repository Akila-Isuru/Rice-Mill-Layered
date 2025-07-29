package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SupplierDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAOImpl implements SupplierDAO {

    @Override
    public List<Supplier> getAll() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM suppliers");
        List<Supplier> suppliers = new ArrayList<>();
        while (rs.next()) {
            suppliers.add(new Supplier(
                    rs.getString("supplier_id"),
                    rs.getString("name"),
                    rs.getString("cotact_number"), // Corrected to cotact_number
                    rs.getString("address"),
                    rs.getString("email")
            ));
        }
        return suppliers;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT supplier_id FROM suppliers ORDER BY supplier_id DESC LIMIT 1");
        char tableChar = 'S';
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            // Handle cases like "S1", "S01", "S001" for parsing
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
    public boolean save(Supplier entity) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO suppliers (supplier_id, name, cotact_number, address, email) VALUES (?,?,?,?,?)",
                entity.getSupplierId(),
                entity.getName(),
                entity.getCotactNumber(), // Corrected to getCotactNumber
                entity.getAddress(),
                entity.getEmail()
        );
    }

    @Override
    public boolean update(Supplier entity) throws SQLException {
        return SQLUtill.execute(
                "UPDATE suppliers SET name=?, cotact_number=?, address=?, email=? WHERE supplier_id=?",
                entity.getName(),
                entity.getCotactNumber(), // Corrected to getCotactNumber
                entity.getAddress(),
                entity.getEmail(),
                entity.getSupplierId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        return SQLUtill.execute(sql, id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        ResultSet rs = SQLUtill.execute("SELECT supplier_id FROM suppliers");
        while(rs.next()){
            ids.add(rs.getString("supplier_id"));
        }
        return ids;
    }

    @Override
    public Optional<Supplier> findById(String id) throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM suppliers WHERE supplier_id = ?", id);
        if (rs.next()) {
            return Optional.of(new Supplier(
                    rs.getString("supplier_id"),
                    rs.getString("name"),
                    rs.getString("cotact_number"), // Corrected to cotact_number
                    rs.getString("address"),
                    rs.getString("email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public int getSupplierCount() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT COUNT(*) FROM suppliers");
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}