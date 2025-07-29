package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDAO extends CrudDAO<Customer, String> {
    List<Customer> search(String text) throws SQLException;
    Optional<Customer> findCustomerByContactNumber(String contactNumber) throws SQLException;
    boolean existsCustomerByContactNumber(String contactNumber) throws SQLException;
    Optional<Customer> findCustomerByEmail(String email) throws SQLException;
    boolean existsCustomerByEmail(String email) throws SQLException;
}