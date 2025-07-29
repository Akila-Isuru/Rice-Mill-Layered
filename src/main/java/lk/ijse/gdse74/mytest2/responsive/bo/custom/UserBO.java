package lk.ijse.gdse74.mytest2.responsive.bo.custom;

import lk.ijse.gdse74.mytest2.responsive.bo.SuperBO;
import lk.ijse.gdse74.mytest2.responsive.dto.Usersdto;

import java.sql.SQLException;
import java.util.List;

public interface UserBO extends SuperBO {
    boolean saveUser(Usersdto dto) throws SQLException, ClassNotFoundException;
    boolean updateUser(Usersdto dto) throws SQLException, ClassNotFoundException;
    boolean deleteUser(String id) throws SQLException, ClassNotFoundException;
    Usersdto searchUser(String id) throws SQLException, ClassNotFoundException;
    List<Usersdto> getAllUsers() throws SQLException, ClassNotFoundException;
    String getNextUserId() throws SQLException, ClassNotFoundException;
    Usersdto authenticateUser(String username, String password) throws SQLException, ClassNotFoundException;
    String getPasswordByEmail(String email) throws SQLException, ClassNotFoundException;
}