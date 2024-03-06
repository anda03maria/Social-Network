package org.example.lab7.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.lab7.StartApplication;
import org.example.lab7.domain.User;
import org.example.lab7.domain.UserParameter;
import org.example.lab7.events.ChangeEvent;
import org.example.lab7.events.ChangeEventType;
import org.example.lab7.service.Service;
import org.example.lab7.service.ServiceException;
import org.example.lab7.utils.observer.Observable;
import org.example.lab7.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EditUserController implements Observable<ChangeEvent> {

    @FXML
    private TextField textFieldUsername;

    @FXML
    private TextField textFieldNewParam;

    @FXML
    private ComboBox<String> comboBoxParameterType;

    private Service service;
    User user;
    private List<Observer<ChangeEvent>> observers = new ArrayList<>();

    private UserController userController;

    public void setUserController(UserController userController) {
        this.userController = userController;
        addObserver(userController);
    }

    @FXML
    private void initialize(){
        if(user!=null)
            setFieldsUPDATE(user);
    }

    public void setService(Service service, User user){
        this.service = service;
        this.user = user;
        initialize();
    }

    private void setFieldsUPDATE(User user) {
        textFieldUsername.setText(user.getId());
        comboBoxParameterType.getItems().addAll("FIRST_NAME", "LAST_NAME", "EMAIL");
        comboBoxParameterType.setValue("FIRST_NAME");
    }

    @FXML
    private void showUserPage(ActionEvent event, User user){
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("user-controller.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("User Page");
            stage.setScene(new Scene(loader.load()));
            UserController userController = loader.getController();
            userController.setService(service, user);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void updateUser(ActionEvent event){
        try {
            String newParam = textFieldNewParam.getText();
            String parameterType = comboBoxParameterType.getValue();
            Optional<User> updateUser = service.findOne(user.getId());
            if(newParam==null || parameterType==null){
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Update user", "Complete all the fields!");
            }
            else {
                if (updateUser.isPresent()) {
                    service.updateUser(user.getId(), newParam, UserParameter.valueOf(parameterType));
                    User updatedUser = service.findOne(user.getId()).get();
                    showUserPage(event, updatedUser);
                    notifyObservers(new ChangeEvent(ChangeEventType.UPDATE, updatedUser));
                }
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Update user", "User updated successfully!");
            }
        } catch (ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @Override
    public void addObserver(Observer<ChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<ChangeEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ChangeEvent data) {
        for (Observer<ChangeEvent> observer : observers) {
            observer.update(data);
        }
    }
}
