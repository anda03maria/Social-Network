package org.example.lab7.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.lab7.domain.FriendRequest;
import org.example.lab7.domain.FriendRequestStatus;
import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.User;
import org.example.lab7.events.ChangeEvent;
import org.example.lab7.events.ChangeEventType;
import org.example.lab7.service.Service;
import org.example.lab7.service.ServiceException;
import org.example.lab7.utils.observer.Observable;
import org.example.lab7.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManageRequestController implements Observable<ChangeEvent> {
    private Service service = Service.getInstance();
    FriendRequest friendRequest;

    UserController userController;

    public void setUserController(UserController userController) {
        this.userController = userController;
        addObserver(userController);
    }

    public void setService(Service service, FriendRequest friendRequest) {
        this.service = service;
        this.friendRequest = friendRequest;
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        try{
            service.updateFriendRequest(friendRequest.getId(), FriendRequestStatus.APPROVED);
            service.addFriendShip(friendRequest.getDela(), friendRequest.getCatre());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "New friend", "Request accepted successfully!");
            notifyObservers(new ChangeEvent(ChangeEventType.ACCEPT, friendRequest.getDela()));
            Stage stage = (Stage)((Button) event.getSource()).getScene().getWindow();
            stage.close();
        }
        catch (ServiceException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleDeny(ActionEvent event) {
        try{
            friendRequest.setStatus(FriendRequestStatus.REJECTED);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Refused friend", "Request denied successfully!");
            notifyObservers(new ChangeEvent(ChangeEventType.REJECT, friendRequest.getDela()));
            Stage stage = (Stage)((Button) event.getSource()).getScene().getWindow();
            stage.close();
        }
        catch (ServiceException ex){
            ex.printStackTrace();
        }
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
