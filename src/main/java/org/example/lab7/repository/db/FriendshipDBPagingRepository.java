package org.example.lab7.repository.db;

import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.StringPair;
import org.example.lab7.domain.User;
import org.example.lab7.repository.paging.Page;
import org.example.lab7.repository.paging.PageImplementation;
import org.example.lab7.repository.paging.Pageable;
import org.example.lab7.repository.paging.PagingRepository;
import org.example.lab7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBPagingRepository extends FriendshipDBRepository  implements PagingRepository<StringPair, Friendship> {
    /**
     * Constructor
     *
     * @param url       address of the database
     * @param username  username for database login
     * @param password  password for database login
     * @param validator entity validator
     */
    public FriendshipDBPagingRepository(String url, String username, String password, Validator<Friendship> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        Set<Friendship> friendshipSet = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ?");

        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*(pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String firstId = resultSet.getString("first_id");
                String secondId = resultSet.getString("second_id");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Friendship friendship = new Friendship(firstId,secondId, friendsFrom);
                friendshipSet.add((friendship));
            }
            return new PageImplementation<Friendship>(pageable,friendshipSet.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
