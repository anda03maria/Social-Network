package org.example.lab7.validator;


import org.example.lab7.domain.User;

import java.util.Optional;

/**
 *  User Validator, used for validating User Objects
 */

public class UserValidator implements Validator<User> {

    /**
     * Validates a name, whether it is first or last name
     * @param name, String, should not have ' ', digits or special characters, excepting '-'
     * @return true, if name is valid, false otherwise
     */
    private boolean validName(String name) {
        if (name == null) {
            return false;
        }

        for (char character : name.toCharArray()) {
            if (!(Character.isLetter(character))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates a username
     * @param userName, String, should not have ' '
     * @return true, if userName is valid, false otherwise
     */
    private boolean validUserName(String userName) {
        if (userName == null) {
            return false;
        }
        //A userName is valid as long as it is not null
        for (char character : userName.toCharArray()) {
            if (character == ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates an email address
     * @param email, String
     * @return true, if email valid, false otherwise
     */
    private boolean validEmail(String email) {
        if (email == null) return false;
        return true;
    }

    /**
     * Validates all the parameters of a User instance
     * @param entity, User
     */
    @Override
    public void validate(Optional<User> entity) throws ValidationException {
        String errorMessage = "";
        if (!validName(entity.get().getFirstName())) {
            errorMessage += "Invalid first name!\n";
        }
        if (!validName(entity.get().getLastName())) {
            errorMessage += "Invalid last name!\n";
        }
        if (!validUserName(entity.get().getId())) {
            errorMessage += "Invalid user name!\n";
        }
        if (!validEmail(entity.get().getEmail())) {
            errorMessage += "Invalid email address!\n";
        }
        if (errorMessage.length() != 0) {
            throw new ValidationException(errorMessage);
        }
    }
}
