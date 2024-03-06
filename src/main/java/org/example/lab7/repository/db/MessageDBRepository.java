package org.example.lab7.repository.db;

import org.example.lab7.domain.Conversation;
import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.Message;
import org.example.lab7.domain.StringPair;
import org.example.lab7.repository.Repository;
import org.example.lab7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MessageDBRepository implements Repository<Integer, Message> {


    private String url;
    private String username;
    private String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Integer integer) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages where id = '%d'", integer);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idConversation = resultSet.getInt("id_conversation");
                String dela = resultSet.getString("dela");
                String catre = resultSet.getString("catre");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                return Optional.of(new Message(id, idConversation, dela, catre, message, date));
            } else {
                return null;
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    @Override
    public Iterable<Optional<Message>> findAll() {
        Set<Optional<Message>> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages order by \"date\" limit 7 offset 0 ");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idConversation = resultSet.getInt("id_conversation");
                String dela = resultSet.getString("dela");
                String catre = resultSet.getString("catre");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message entity = new Message(id, idConversation, dela, catre, message, date);
                entities.add(Optional.of(entity));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return entities;
    }

    @Override
    public boolean save(Message entity) {
        String sql = String.format("insert into messages(id_conversation, dela, catre, message, date) " +
                        "values(%d, '%s', '%s', '%s', '%s')",
                entity.getIdConversation(),
                entity.getFrom(),
                entity.getTo(),
                entity.getMessage(),
                entity.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));;
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
    public Optional<Message> delete(Integer integer) {
        Message entity = findOne(integer).get();
        if (entity == null) {
            return null;
        } else {
            String sql = String.format("delete from messages where id_message = %d", integer);
            try(Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
                return Optional.of(entity);
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    public Iterable<Message> getCustomList(String sqlStatement) {
        Set<Message> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     sqlStatement);
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idConversation = resultSet.getInt("id_conversation");
                String dela = resultSet.getString("dela");
                String catre = resultSet.getString("catre");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message entity = new Message(id, idConversation, dela, catre, message, date);
                entities.add(entity);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return entities;
    }

    @Override
    public boolean update(Message entity) {
        return false;
    }
}
