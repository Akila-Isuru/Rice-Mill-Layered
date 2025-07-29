package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.RawPaddydto;

import java.sql.SQLException;
import java.util.List;

public interface RawPaddyBO extends SuperBO {
    List<RawPaddydto> getAllRawPaddy() throws SQLException;
    void saveRawPaddy(RawPaddydto dto) throws DuplicateException, Exception;
    void updateRawPaddy(RawPaddydto dto) throws NotFoundException, Exception;
    boolean deleteRawPaddy(String id) throws InUseException, NotFoundException, Exception;
    String getNextId() throws SQLException;
    List<String> getAllRawPaddyIds() throws SQLException; // For dropdowns/lookups
}