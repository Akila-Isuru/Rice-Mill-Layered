package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.SupplierDAO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.entity.Supplier; // Import the new entity

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
            Supplier supplier = new Supplier(
                    rs.getString("supplier_id"),
                    rs.getString("name"),
                    rs.getString("cotact_number"), // Use database column name
                    rs.getString("address"),
                    rs.getString("email")
            );
            suppliers.add(supplier);
        }
        return suppliers;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT supplier_id FROM suppliers ORDER BY supplier_id DESC LIMIT 1");
        char tableChar = 'S';
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
    public boolean save(Supplier supplier) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO suppliers (supplier_id, name, cotact_number, address, email) VALUES (?,?,?,?,?)",
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getContactNumber(), // Use DTO field name, but maps to 'cotact_number' column
                supplier.getAddress(),
                supplier.getEmail()
        );
    }

    @Override
    public boolean update(Supplier supplier) throws SQLException {
        return SQLUtill.execute(
                "UPDATE suppliers SET name=?, cotact_number=?, address=?, email=? WHERE supplier_id=?",
                supplier.getName(),
                supplier.getContactNumber(), // Use DTO field name, but maps to 'cotact_number' column
                supplier.getAddress(),
                supplier.getEmail(),
                supplier.getSupplierId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM suppliers WHERE supplier_id=?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT supplier_id FROM suppliers");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Supplier> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM suppliers WHERE supplier_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Supplier(
                    resultSet.getString("supplier_id"),
                    resultSet.getString("name"),
                    resultSet.getString("cotact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Supplier> findSupplierByContactNumber(String contactNumber) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM suppliers WHERE cotact_number = ?", contactNumber);
        if (resultSet.next()) {
            return Optional.of(new Supplier(
                    resultSet.getString("supplier_id"),
                    resultSet.getString("name"),
                    resultSet.getString("cotact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Supplier> findSupplierByEmail(String email) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM suppliers WHERE email = ?", email);
        if (resultSet.next()) {
            return Optional.of(new Supplier(
                    resultSet.getString("supplier_id"),
                    resultSet.getString("name"),
                    resultSet.getString("cotact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            ));
        }
        return Optional.empty();
    }
}