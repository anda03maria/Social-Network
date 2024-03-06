package org.example.lab7.repository.db;

import org.example.lab7.domain.User;
import org.example.lab7.repository.Repository;
import org.example.lab7.validator.Validator;
import java.sql.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for storing users in database
 */
public class UserDBRepository implements Repository<String, User> {

    protected String url;
    protected String username;
    protected String password;

    private Validator<User> validator;


    /**
     * Constructor
     * @param url address of the database
     * @param username username for database login
     * @param password password for database login
     * @param validator entity validator
     */
    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }

    /**
     * Searches for a user based on a given ID
     * @param username -the id of the entity to be returned
     *           id must not be null
     * @return the user, if found or null
     */
    @Override
    public Optional<User> findOne(String username) {
        try(Connection connection = DriverManager.getConnection(url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where username = ?");

        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(username,firstName,lastName, email, password);
                u.setId(username);
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * @return all the users stored in the database
     */
    @Override
    public Iterable<Optional<User>> findAll() {
        Set<Optional<User>> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(username,firstName,lastName, email, password);
                user.setId(username);
                users.add(Optional.of(user));
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a new user in the database, if valid
     * @param user
     *         entity must be not null
     * @return true if the entity was successfully saved, false otherwise
     */
    @Override
    public boolean save(User user) {
        if (user == null) throw new IllegalArgumentException("entity must not be null!");
        Optional<User> foundUser = findOne(user.getId());
        if (!foundUser.isEmpty()) return false;
        validator.validate(Optional.of(user));
        String sql = "INSERT INTO users(username, first_name, last_name, email, password) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getId());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getEmail());
            statement.setString(5,user.getPassword());
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a user based on a given ID and all their friendships
     * @param username
     *      id must be not null
     * @return the user which was deleted or null if nothing was deleted
     */
    @Override
    public Optional<User> delete(String username) {
        User user = findOne(username).get();
        if (user == null) {
            return null;
        } else {
            /*
            ALTER TABLE friendships
                ADD CONSTRAINT fk_first_id FOREIGN KEY (first_id) REFERENCES users(username) ON DELETE CASCADE,
                ADD CONSTRAINT fk_second_id FOREIGN KEY (second_id) REFERENCES users(username) ON DELETE CASCADE;
             */
            String sql = String.format("DELETE FROM users " +
                    "WHERE username LIKE ?");
            try(Connection connection = DriverManager.getConnection(url, this.username, this.password);
                PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.executeUpdate();
                return Optional.of(user);
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Updated an entity from the database
     * @param user
     *          entity must not be null
     * @return true, if the entity was successfully updated, false otherwise
     */
    @Override
    public boolean update(User user) {
        if (user == null) throw new IllegalArgumentException("entity must be not null!");
        validator.validate(Optional.of(user));
        String sql = String.format("UPDATE users SET " + "first_name = '%s', last_name = '%s', email = '%s'" +
                        "WHERE username LIKE '%s'",
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getId());
        try (Connection connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public Iterable<User> getCustomList(String format) {
        return null;
    }
}
