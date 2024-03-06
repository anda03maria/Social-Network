package org.example.lab7.repository.db;


import org.example.lab7.domain.User;
import org.example.lab7.repository.paging.Page;
import org.example.lab7.repository.paging.PageImplementation;
import org.example.lab7.repository.paging.Pageable;
import org.example.lab7.repository.paging.PagingRepository;
import org.example.lab7.validator.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDBPagingRepository extends UserDBRepository  implements PagingRepository<String, User>
{

    public UserDBPagingRepository(String url, String username, String password, Validator<User> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");

        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*(pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(username, firstName, lastName, email, password);
                user.setId(username);
                users.add(user);

            }
            return new PageImplementation<User>(pageable,users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


//    @Override
//    public Page<User> findAll(Pageable pageable) {
//        Stream<User> result;
//        result = StreamSupport.stream(this.findAll().spliterator(), false)
//                .skip(pageable.getPageNumber()  * pageable.getPageSize())
//                .limit(pageable.getPageSize());
//        return new PageImplementation<>(pageable, result);
//    }
}
