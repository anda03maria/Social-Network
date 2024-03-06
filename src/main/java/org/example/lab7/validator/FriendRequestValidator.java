package org.example.lab7.validator;

import org.example.lab7.domain.FriendRequest;

import java.util.Optional;

public class FriendRequestValidator implements Validator<FriendRequest>{

    /**
     * Validates a FriendRequest Object
     * @param entity, Friendship
     */
    @Override
    public void validate(Optional<FriendRequest> entity) throws ValidationException {
        String errorMessage = "";
        String dela = entity.get().getDela();
        String catre = entity.get().getCatre();
        if (dela == null || catre == null || dela.equals(catre)) {
            errorMessage += "Invalid friendship!\n";
        }
        if (errorMessage.length() > 0) {
            throw new ValidationException(errorMessage);
        }
    }
}
