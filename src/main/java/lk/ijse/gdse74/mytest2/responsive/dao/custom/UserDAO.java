package lk.ijse.gdse74.mytest2.responsive.dao.custom;

import lk.ijse.gdse74.mytest2.responsive.dao.CrudDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.User;

import java.sql.SQLException;

public interface UserDAO extends CrudDAO<User, String> {
    User search(String id) throws SQLException, ClassNotFoundException;


    User getUserByUsernameAndPassword(String username, String password) throws SQLException, ClassNotFoundException;
    String getPasswordByEmail(String email) throws SQLException, ClassNotFoundException;
    String getLastId() throws SQLException, ClassNotFoundException;

}