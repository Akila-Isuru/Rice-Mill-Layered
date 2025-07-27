package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.CustomerDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public List<Customer> getAll() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM customers");

        List<Customer> list = new ArrayList<>();
        while (resultSet.next()) {
            Customer customer = new Customer(
                    resultSet.getString("customer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            );
            list.add(customer);
        }
        return list;
    }

    @Override
    public String getNextId() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1");
        char tableChar = 'C';
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
    public boolean save(Customer customer) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO customers (customer_id, name, contact_number, address, email) VALUES (?, ?, ?, ?, ?)",
                customer.getCustomerId(),
                customer.getName(),
                customer.getContactNumber(),
                customer.getAddress(),
                customer.getEmail()
        );
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        return SQLUtill.execute(
                "UPDATE customers SET name = ?, contact_number = ?, address = ?, email = ? WHERE customer_id = ?",
                customer.getName(),
                customer.getContactNumber(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getCustomerId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM customers WHERE customer_id = ?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT customer_id FROM customers");
        List<String> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getString(1));
        }
        return ids;
    }

    @Override
    public Optional<Customer> findById(String id) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM customers WHERE customer_id = ?", id);
        if (resultSet.next()) {
            return Optional.of(new Customer(
                    resultSet.getString("customer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> search(String text) throws SQLException {
        String searchText = "%" + text + "%";
        ResultSet resultSet = SQLUtill.execute(
                "SELECT * FROM customers WHERE customer_id LIKE ? OR name LIKE ? OR contact_number LIKE ? OR address LIKE ? OR email LIKE ?",
                searchText, searchText, searchText, searchText, searchText
        );

        List<Customer> list = new ArrayList<>();
        while (resultSet.next()) {
            Customer customer = new Customer(
                    resultSet.getString("customer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            );
            list.add(customer);
        }
        return list;
    }

    @Override
    public Optional<Customer> findCustomerByContactNumber(String contactNumber) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM customers WHERE contact_number = ?", contactNumber);
        if (resultSet.next()) {
            return Optional.of(new Customer(
                    resultSet.getString("customer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsCustomerByContactNumber(String contactNumber) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM customers WHERE contact_number = ?", contactNumber);
        return resultSet.next();
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM customers WHERE email = ?", email);
        if (resultSet.next()) {
            return Optional.of(new Customer(
                    resultSet.getString("customer_id"),
                    resultSet.getString("name"),
                    resultSet.getString("contact_number"),
                    resultSet.getString("address"),
                    resultSet.getString("email")
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsCustomerByEmail(String email) throws SQLException {
        ResultSet resultSet = SQLUtill.execute("SELECT * FROM customers WHERE email = ?", email);
        return resultSet.next();
    }
}