package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.CustomerBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.bo.util.EntityDTOConverter;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.CustomerDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;
import lk.ijse.gdse74.mytest2.responsive.entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerBOImpl implements CustomerBO {

    private final CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOTypes.CUSTOMER);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<Customersdto> getAllCustomers() throws SQLException {
        List<Customer> customers = customerDAO.getAll();
        List<Customersdto> customerDTOS = new ArrayList<>();
        for (Customer customer : customers) {
            customerDTOS.add(converter.getCustomerDTO(customer));
        }
        return customerDTOS;
    }

    @Override
    public void saveCustomer(Customersdto dto) throws DuplicateException, Exception {
        Optional<Customer> optionalCustomer = customerDAO.findById(dto.getCustomerId());
        if (optionalCustomer.isPresent()) {
            throw new DuplicateException("Duplicate customer id");
        }

        Optional<Customer> customerByContactOptional = customerDAO.findCustomerByContactNumber(dto.getContactNumber());
        if (customerByContactOptional.isPresent()) {
            throw new DuplicateException("Duplicate customer contact number");
        }

        if (customerDAO.existsCustomerByEmail(dto.getEmail())) {
            throw new DuplicateException("Duplicate customer email");
        }

        Customer customer = converter.getCustomer(dto);
        customerDAO.save(customer);
    }

    @Override
    public void updateCustomer(Customersdto dto) throws SQLException {
        Optional<Customer> optionalCustomer = customerDAO.findById(dto.getCustomerId());
        if (optionalCustomer.isEmpty()) {
            throw new NotFoundException("Customer not found");
        }

        Optional<Customer> customerByContactOptional = customerDAO.findCustomerByContactNumber(dto.getContactNumber());
        if (customerByContactOptional.isPresent()) {
            Customer customer = customerByContactOptional.get();
            if (!customer.getCustomerId().equals(dto.getCustomerId())) {
                throw new DuplicateException("Duplicate contact number");
            }
        }

        Optional<Customer> customerByEmailOptional = customerDAO.findCustomerByEmail(dto.getEmail());
        if (customerByEmailOptional.isPresent()) {
            Customer customer = customerByEmailOptional.get();
            if (!customer.getCustomerId().equals(dto.getCustomerId())) {
                throw new DuplicateException("Duplicate email");
            }
        }

        Customer customer = converter.getCustomer(dto);
        customerDAO.update(customer);
    }

    @Override
    public boolean deleteCustomer(String id) throws InUseException, Exception {
        Optional<Customer> optionalCustomer = customerDAO.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new NotFoundException("Customer not found..!");
        }

        try {
            return customerDAO.delete(id);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getNextId() throws SQLException {
        return customerDAO.getNextId();
    }
}