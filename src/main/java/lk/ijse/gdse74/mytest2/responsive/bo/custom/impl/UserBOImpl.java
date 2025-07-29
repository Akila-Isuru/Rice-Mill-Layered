package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.UserBO;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.UserDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.Usersdto;
import lk.ijse.gdse74.mytest2.responsive.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserBOImpl implements UserBO {

    private UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOTypes.USER); // Initialize DAO

    @Override
    public boolean saveUser(Usersdto dto) throws SQLException, ClassNotFoundException {
        User entity = new User(dto.getUser_id(), dto.getName(), dto.getEmail(), dto.getRole(), dto.getContact_number(), dto.getPassword());
        return userDAO.save(entity);
    }

    @Override
    public boolean updateUser(Usersdto dto) throws SQLException, ClassNotFoundException {
        User entity = new User(dto.getUser_id(), dto.getName(), dto.getEmail(), dto.getRole(), dto.getContact_number(), dto.getPassword());
        return userDAO.update(entity);
    }

    @Override
    public boolean deleteUser(String id) throws SQLException, ClassNotFoundException {
        return userDAO.delete(id);
    }

    @Override
    public Usersdto searchUser(String id) throws SQLException, ClassNotFoundException {
        User entity = userDAO.search(id);
        if (entity != null) {
            return new Usersdto(entity.getUserId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.getContactNumber());
        }
        return null;
    }

    @Override
    public List<Usersdto> getAllUsers() throws SQLException, ClassNotFoundException {
        List<User> entities = userDAO.getAll();
        List<Usersdto> dtos = new ArrayList<>();
        for (User entity : entities) {
            dtos.add(new Usersdto(entity.getUserId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.getContactNumber()));
        }
        return dtos;
    }

    @Override
    public String getNextUserId() throws SQLException, ClassNotFoundException {
        String lastId = userDAO.getLastId();
        char tableChar = 'U';
        if (lastId == null) {
            return tableChar + "001";
        } else {
            int lastIdNumber = Integer.parseInt(lastId.substring(1));
            int nextIdNumber = lastIdNumber + 1;
            return String.format(tableChar + "%03d", nextIdNumber);
        }
    }

    @Override
    public Usersdto authenticateUser(String username, String password) throws SQLException, ClassNotFoundException {
        User entity = userDAO.getUserByUsernameAndPassword(username, password);
        if (entity != null) {
            return new Usersdto(entity.getUserId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.getContactNumber());
        }
        return null;
    }

    @Override
    public String getPasswordByEmail(String email) throws SQLException, ClassNotFoundException {
        return userDAO.getPasswordByEmail(email);
    }
}