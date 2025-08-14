package lk.ijse.gdse74.mytest2.responsive.dao.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.dao.SQLUtill;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.UserDAO;
import lk.ijse.gdse74.mytest2.responsive.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(User entity) throws SQLException {
        return SQLUtill.execute(
                "INSERT INTO users (user_id, name, email, role, contact_number, password) VALUES (?,?,?,?,?,?)",
                entity.getUserId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getContactNumber(),
                entity.getPassword()
        );
    }

    @Override
    public boolean update(User entity) throws SQLException {
        return SQLUtill.execute(
                "UPDATE users SET name=?, email=?, role=?, contact_number=?, password=? WHERE user_id=?",
                entity.getName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getContactNumber(),
                entity.getPassword(),
                entity.getUserId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return SQLUtill.execute("DELETE FROM users WHERE user_id=?", id);
    }

    @Override
    public List<String> getAllIds() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<User> findById(String id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public User search(String id) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM users WHERE user_id = ?", id);
        if (rs.next()) {
            return new User(
                    rs.getString("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("contact_number"),
                    rs.getString("password")
            );
        }
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM users");
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(
                    rs.getString("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("contact_number"),
                    rs.getString("password")
            ));
        }
        return users;
    }

    @Override
    public String getNextId() throws SQLException {
        return "";
    }

    @Override
    public User getUserByUsernameAndPassword(String username, String password) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtill.execute("SELECT * FROM users WHERE name = ? AND password = ?", username, password);
        if (rs.next()) {
            return new User(
                    rs.getString("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("contact_number"),
                    rs.getString("password")
            );
        }
        return null;
    }

    @Override
    public String getPasswordByEmail(String email) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtill.execute("SELECT password FROM users WHERE email = ?", email);
        return rs.next() ? rs.getString("password") : null;
    }

    @Override
    public String getLastId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtill.execute("SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1");
        if (resultSet.next()) {
            return resultSet.getString("user_id");
        }
        return null;
    }
}