package org.example.lab7.repository.db;

import org.example.lab7.domain.Conversation;
import org.example.lab7.domain.Message;
import org.example.lab7.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ConversationDBRepository implements Repository<Integer, Conversation> {

    protected String url;
    protected String username;
    protected String password;

    public ConversationDBRepository(String url, String userName, String password) {
        this.url = url;
        this.username = userName;
        this.password = password;
    }

    @Override
    public Optional<Conversation> findOne(Integer integer) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from conversation where id_conversation = '%d'", integer);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int id_conversation = resultSet.getInt("id_conversation");
                String firstUser = resultSet.getString("first_user");
                String secondUser = resultSet.getString("second_user");
                return Optional.of(new Conversation(id_conversation, firstUser, secondUser));
            } else {
                return null;
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    @Override
    public Iterable<Optional<Conversation>> findAll() {
        Set<Optional<Conversation>> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from conversation");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id_conversation = resultSet.getInt("id_conversation");
                String firstUser = resultSet.getString("first_user");
                String secondUser = resultSet.getString("second_user");
                Conversation entity = new Conversation(id_conversation, firstUser, secondUser);
                entities.add(Optional.of(entity));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return entities;
    }

    @Override
    public boolean save(Conversation entity) {
        String sql = String.format("insert into conversation(first_user, second_user) " +
                "values('%s', '%s')", entity.getFirstUser(), entity.getSecondUser());
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Conversation> delete(Integer integer) {
        Conversation entity = findOne(integer).get();
        if (entity == null) {
            return null;
        } else {
            String sql = String.format("delete from conversation where id_conversation = %d", integer);
            try(Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
                return Optional.of(entity);
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        }    }

    @Override
    public boolean update(Conversation entity) {
        return false;
    }

    /**
     * Returns a query based on a custom sql query
     * @param sqlStatement
     * @return
     */

    @Override
    public Iterable<Conversation> getCustomList(String sqlStatement) {
        Set<Conversation> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     sqlStatement);
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id_conversation = resultSet.getInt("id_conversation");
                String firstUser = resultSet.getString("first_user");
                String secondUser = resultSet.getString("second_user");
                Conversation entity = new Conversation(id_conversation, firstUser, secondUser);
                entities.add(entity);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return entities;
    }
}
