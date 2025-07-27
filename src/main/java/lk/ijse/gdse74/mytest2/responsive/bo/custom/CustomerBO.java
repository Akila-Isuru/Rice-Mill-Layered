package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;

import java.sql.SQLException;
import java.util.List;

public interface CustomerBO extends SuperBO {
    List<Customersdto> getAllCustomers() throws SQLException;
    void saveCustomer(Customersdto dto) throws DuplicateException, Exception;
    void updateCustomer(Customersdto dto) throws SQLException;
    boolean deleteCustomer(String id) throws InUseException, Exception;
    String getNextId() throws SQLException;
}