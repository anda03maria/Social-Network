package org.example.lab7.repository.db;


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

public class FriendshipDBRepository implements Repository<StringPair, Friendship> {

    protected String url;
    protected String username;
    protected String password;

    private Validator<Friendship> validator;


    /**
     * Constructor
     * @param url address of the database
     * @param username username for database login
     * @param password password for database login
     * @param validator entity validator
     */
    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Optional<Friendship> findOne(StringPair id) {
        try(Connection connection = DriverManager.getConnection(url, this.username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships " +
                    "WHERE (first_id LIKE ? AND second_id LIKE ?) OR (first_id LIKE ? AND second_id LIKE ?)")) {
            statement.setString(1, id.getFirst());
            statement.setString(2, id.getSecond());
            statement.setString(3, id.getSecond());
            statement.setString(4, id.getFirst());

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstId = resultSet.getString("first_id");
                String secondId = resultSet.getString("second_id");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                return Optional.of(new Friendship(firstId, secondId,friendsFrom));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Optional<Friendship>> findAll() {
        Set<Optional<Friendship>> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next())
            {
                String firstId = resultSet.getString("first_id");
                String secondId = resultSet.getString("second_id");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Friendship friendship = new Friendship(firstId,secondId, friendsFrom);
                friendships.add(Optional.of(friendship));
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean save(Friendship friendship) {
        if (friendship == null) throw new IllegalArgumentException("entity must not be null!");
        Optional<Friendship> foundFriendship = findOne(friendship.getId());
        if (!foundFriendship.isEmpty()) {
            return false;
        }
        validator.validate(Optional.ofNullable(friendship));
        String sql = "INSERT INTO friendships(first_id, second_id, friends_from) VALUES " +
                String.format("('%s', '%s', '%s')",
                        friendship.getId().getFirst(),
                        friendship.getId().getSecond(),
                        friendship.getFriendsFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
    public Optional<Friendship> delete(StringPair id) {
        Optional<Friendship> optionalFriendship = findOne(id);
        if(optionalFriendship.isPresent()){
            String sql = String.format("DELETE FROM friendships " +
                    "WHERE (first_id LIKE ? AND second_id LIKE ?) OR (first_id LIKE ? AND second_id LIKE ?)");
            try(Connection connection = DriverManager.getConnection(url, this.username, password);
                PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id.getFirst());
                statement.setString(2, id.getSecond());
                statement.setString(3, id.getSecond());
                statement.setString(4, id.getFirst());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    return optionalFriendship;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Friendship friendship) {
        if (friendship == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        validator.validate(Optional.of(friendship));
        String sql = String.format("UPDATE friendships SET " +
                        "friends_from = '%s' WHERE first_id = '%s' AND second_id = '%s'",
                friendship.getFriendsFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                friendship.getId().getFirst(),
                friendship.getId().getSecond());
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public Iterable<Friendship> getCustomList(String format) {
        return null;
    }

}
