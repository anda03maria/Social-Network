package org.example.lab7.validator;


import org.example.lab7.domain.Friendship;

import java.util.Optional;

/**
 * Friendship Validator, used for validating a Friendship Object
 */
public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Validates a Friendship Object
     * @param entity, Friendship
     */
    @Override
    public void validate(Optional<Friendship> entity) throws ValidationException {
        String errorMessage = "";
        String firstId = entity.get().getId().getFirst();
        String secondId = entity.get().getId().getSecond();
        if (firstId == null || secondId == null || firstId.equals(secondId)) {
            errorMessage += "Invalid friendship!\n";
        }
        if (errorMessage.length() > 0) {
            throw new ValidationException(errorMessage);
        }
    }
}
