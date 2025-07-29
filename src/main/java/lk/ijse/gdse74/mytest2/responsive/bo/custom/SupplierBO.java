package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto;

import java.sql.SQLException;
import java.util.List;

public interface SupplierBO extends SuperBO {
    List<Suppliersdto> getAllSuppliers() throws SQLException;
    void saveSupplier(Suppliersdto dto) throws DuplicateException, Exception;
    void updateSupplier(Suppliersdto dto) throws NotFoundException, Exception;
    boolean deleteSupplier(String id) throws InUseException, NotFoundException, Exception;
    String getNextId() throws SQLException;
    List<String> getAllSupplierIds() throws SQLException;
    int getSupplierCount() throws Exception;
}