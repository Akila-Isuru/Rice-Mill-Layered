package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException; // Added NotFoundException to throws clause
import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto;

import java.sql.SQLException;
import java.util.List;

public interface MillingProcessBO extends SuperBO {
    List<MillingProcessdto> getAllMillingProcesses() throws SQLException;
    void saveMillingProcess(MillingProcessdto dto) throws DuplicateException, SQLException;
    void updateMillingProcess(MillingProcessdto dto) throws SQLException, NotFoundException; // Added NotFoundException
    boolean deleteMillingProcess(String id) throws InUseException, SQLException, NotFoundException; // Added NotFoundException
    String getNextMillingProcessId() throws SQLException;
    boolean checkPaddyIdExistsInProcess(String paddyId) throws SQLException;
    MillingProcessdto getMillingProcessByMillingId(String millingId) throws SQLException, ClassNotFoundException, NotFoundException; // Added NotFoundException


    List<String> getAllMillingProcessIds() throws SQLException, ClassNotFoundException;
}