package org.example.lab7.repository.db;

import org.example.lab7.domain.FriendRequest;
import org.example.lab7.domain.FriendRequestStatus;
import org.example.lab7.domain.Friendship;
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

public class FriendRequestDBPagingRepository extends FriendRequestDBRepository  implements PagingRepository<Long, FriendRequest> {
    /**
     * Constructor
     *
     * @param url       address of the database
     * @param username  username for database login
     * @param password  password for database login
     * @param validator entity validator
     */
    public FriendRequestDBPagingRepository(String url, String username, String password, Validator<FriendRequest> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        Set<FriendRequest> friendRequests = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend_request limit ? offset ?");

        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*(pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String dela = resultSet.getString("dela");
                String catre = resultSet.getString("catre");
                FriendRequestStatus status = FriendRequestStatus.valueOf(resultSet.getString("status"));
                FriendRequest friendRequest = new FriendRequest(id, dela, catre,status);
                friendRequests.add((friendRequest));
            }
            return new PageImplementation<FriendRequest>(pageable,friendRequests.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
