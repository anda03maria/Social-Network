package org.example.lab7.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.lab7.StartApplication;
import org.example.lab7.domain.User;
import org.example.lab7.repository.paging.Page;
import org.example.lab7.service.Service;
import org.example.lab7.service.ServiceException;
import org.example.lab7.service.StringHash;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    Slider pageSlider = new Slider(1,10,1);

    @FXML
    ComboBox<String> entityComboBox;


    private Service service = Service.getInstance();

    public LoginController() {
    }

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        valueLabel.setText("0");
        setPageSlider();
        entityComboBox.getItems().addAll("user", "friendship", "request", "message");
        entityComboBox.setValue("user");
    }

    @FXML
    private Label valueLabel;

    public void setPageSlider(){
        pageSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int selectedValue = newValue.intValue();
            valueLabel.setText(String.valueOf(selectedValue));
        });
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
    private void handleLogin(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (!username.isEmpty() && !password.isEmpty()) {
            Optional<User> user = service.findOne(username);
            if(user.isEmpty()){
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Login failed", "User doesn't exist! Create a new one!");
            }
            else {
                User u = user.get();
                String encryptedPassword = StringHash.toHexString(StringHash.getSHA(password));
                if(encryptedPassword.equals(u.getPassword())){
                    try {
                        showUserPage(actionEvent, user.get());
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                } else {
                    MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Login failed", "Incorrect password!");
                }
            }
        } else {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Login failed", "Invalid username!");
        }
    }

    private void addUser(String firstName, String lastName, String username, String email, String password) {
        try{
            service.addUser(username, firstName, lastName, email, password);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Create User","User added successfully!");
        }catch (ServiceException e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSignup(ActionEvent actionEvent){
        String username = usernameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Sign Up Failed", "Please fill in all fields!");
        } else {
            addUser(firstName, lastName, username, email, password);
            showUserPage(actionEvent, new User(username, firstName, lastName, email, password));
        }
    }

    @FXML
    private void handlePaging(ActionEvent actionEvent){
        int pageSize = (int) pageSlider.getValue();
        String entityText = entityComboBox.getValue();
        service.setPageSize(pageSize);
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("page-controller.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Pagination");
            stage.setScene(new Scene(loader.load()));
            PageController pageController = loader.getController();
            pageController.setService(service, pageSize, entityText);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
