package org.example.lab7.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.lab7.domain.Friendship;
import org.example.lab7.events.ChangeEvent;
import org.example.lab7.events.ChangeEventType;
import org.example.lab7.service.Service;
import org.example.lab7.utils.observer.Observable;
import org.example.lab7.utils.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;

public class FriendshipController implements Observable<ChangeEvent> {
    Service service;

    Friendship friendship;

    @FXML
    Label dateLabel;

    @FXML
    public void initialize(){
        if(friendship!=null){
            dateLabel.setText(friendship.getFriendsFrom().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
    }

    public void setService(Service service, Friendship friendship){
        this.service = service;
        this.friendship = friendship;
        initialize();
    }


    @FXML
    public void handleDeleteFriendship(ActionEvent actionEvent){
        if(friendship != null){
            Friendship deletedFriendship = friendship;
            service.removeFriendship(friendship.getId().getFirst(),friendship.getId().getSecond());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Remove friendship","Friendship removed successfully!");
            notifyObservers(new ChangeEvent(ChangeEventType.DELETE, deletedFriendship));
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
        else {
            MessageAlert.showErrorMessage(null,"No friendship was selected!");
        }

    }

    UserController userController;

    public void setUserController(UserController userController) {
        this.userController = userController;
        addObserver(userController);
    }

    private List<Observer<ChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(ChangeEvent changeEvent) {
        for (Observer<ChangeEvent> observer : observers) {
            observer.update(changeEvent);
        }
    }
}
