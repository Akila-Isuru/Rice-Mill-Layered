package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.FarmerDAO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer; // Import the new Farmer entity

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FarmerDAOImpl implements FarmerDAO {

    @Override
    public List<Farmer> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM farmers");

        List<Farmer> list = new ArrayList<>();
        while (resultSet.next()) {
            Farmer farmer = new Farmer(
                    resultSet.getString("farmer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address")
            );
            list.add(farmer);
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT farmer_id FROM farmers ORDER BY farmer_id DESC LIMIT 1");
        char tableChar = 'F'; // 'F' for Farmer
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
    public boolean save(Farmer farmer) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO farmers (farmer_id, name, contact_number, address) VALUES (?, ?, ?, ?)",
                farmer.getFarmerId(),
                farmer.getName(),
                farmer.getContactNumber(),
                farmer.getAddress()
        );
    }

    @Override
    public boolean update(Farmer farmer) throws SQLException {
        return SQLUtill.execute(
                "UPDATE farmers SET name = ?, contact_number = ?, address = ? WHERE farmer_id = ?",
                farmer.getName(),
                farmer.getContactNumber(),
                farmer.getAddress(),
                farmer.getFarmerId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM farmers WHERE farmer_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT farmer_id FROM farmers");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Farmer> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM farmers WHERE farmer_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Farmer(
                    resultSet.getString("farmer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<Farmer> search(String text) throws SQLException {
        String searchText = "%" + text + "%";
        ResultSet resultSet = SQLUtill.execute(
                "SELECT * FROM farmers WHERE farmer_id LIKE ? OR name LIKE ? OR contact_number LIKE ? OR address LIKE ?",
                searchText, searchText, searchText, searchText
        );

        List<Farmer> list = new ArrayList<>();
        while (resultSet.next()) {
            Farmer farmer = new Farmer(
                    resultSet.getString("farmer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address")
            );
            list.add(farmer);
        }
        return list;
    }

    @Override
    public Optional<Farmer> findFarmerByContactNumber(String contactNumber) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM farmers WHERE contact_number = ?", contactNumber);
        if (resultSet.next()) {
            return Optional.of(new Farmer(
                    resultSet.getString("farmer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address")
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsFarmerByContactNumber(String contactNumber) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM farmers WHERE contact_number = ?", contactNumber);
        return resultSet.next();
    }
}