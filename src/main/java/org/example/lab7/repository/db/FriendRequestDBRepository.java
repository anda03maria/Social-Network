package org.example.lab7.repository.db;

import org.example.lab7.domain.FriendRequest;
import org.example.lab7.domain.FriendRequestStatus;
import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.StringPair;
import org.example.lab7.repository.Repository;
import org.example.lab7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendRequestDBRepository implements Repository<Long, FriendRequest> {

    protected String url;
    protected String username;
    protected String password;

    private Validator<FriendRequest> validator;

    /**
     * Constructor
     * @param url address of the database
     * @param username username for database login
     * @param password password for database login
     * @param validator entity validator
     */
    public FriendRequestDBRepository(String url, String username, String password, Validator<FriendRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public Optional<FriendRequest> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, this.username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friend_request where id = ?")) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String dela = resultSet.getString("dela");
                String catre = resultSet.getString("catre");
                FriendRequestStatus status = FriendRequestStatus.valueOf(resultSet.getString("status"));
                return Optional.of(new FriendRequest(id, dela, catre,status));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Optional<FriendRequest>> findAll() {
        Set<Optional<FriendRequest>> friend_requests = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend_request");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String dela = resultSet.getString("dela");
                String catre = resultSet.getString("catre");
                FriendRequestStatus status = FriendRequestStatus.valueOf(resultSet.getString("status"));
                FriendRequest friendRequest = new FriendRequest(id, dela, catre, status);
                friend_requests.add(Optional.of(friendRequest));
            }
            return friend_requests;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean save(FriendRequest entity) {
        validator.validate(Optional.ofNullable(entity));
        String sql = "INSERT INTO friend_request(dela, catre, status) VALUES " +
                String.format("('%s', '%s', '%s')",
                        entity.getDela(),
                        entity.getCatre(),
                        entity.getStatus());
        try (Connection connection = DriverManager.getConnection(url, this.username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<FriendRequest> delete(Long id) {
        FriendRequest friendRequest = findOne(id).get();
        if (friendRequest == null) {
            return null;
        } else {
            String sql = String.format("DELETE FROM friend_request " +
                            "WHERE id = '%d'", id);
            try(Connection connection = DriverManager.getConnection(url, this.username, password);
                PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
                return Optional.of(friendRequest);
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public boolean update(FriendRequest entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        validator.validate(Optional.of(entity));
        String sql = String.format("UPDATE friend_request SET " +
                        "status = '%s' WHERE id = '%d'",
                entity.getStatus(),
                entity.getId());
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            return false;
        }    }

    @Override
    public Iterable<FriendRequest> getCustomList(String format) {
        return null;
    }

}
