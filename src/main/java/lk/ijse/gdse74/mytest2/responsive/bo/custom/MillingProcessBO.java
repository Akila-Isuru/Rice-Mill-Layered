package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto; // Using the existing DTO name

import java.sql.SQLException;
import java.util.List;

public interface MillingProcessBO extends SuperBO {
    List<MillingProcessdto> getAllMillingProcesses() throws SQLException;
    void saveMillingProcess(MillingProcessdto dto) throws DuplicateException, Exception;
    void updateMillingProcess(MillingProcessdto dto) throws SQLException;
    boolean deleteMillingProcess(String id) throws InUseException, Exception;
    String getNextMillingProcessId() throws SQLException;
    List<String> getAllPaddyIdsForMilling() throws SQLException; // Adjusted method name
    boolean checkPaddyIdExistsInProcess(String paddyId) throws SQLException;
}