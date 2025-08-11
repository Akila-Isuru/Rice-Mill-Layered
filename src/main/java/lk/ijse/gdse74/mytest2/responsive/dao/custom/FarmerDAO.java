package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Farmer; // Import the new Farmer entity

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FarmerDAO extends CrudDAO<Farmer, String> {
    List<Farmer> search(String text) throws SQLException;
    Optional<Farmer> findFarmerByContactNumber(String contactNumber) throws SQLException;
    boolean existsFarmerByContactNumber(String contactNumber) throws SQLException;
}