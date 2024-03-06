package org.example.lab7.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.example.lab7.StartApplication;
import org.example.lab7.domain.FriendRequest;
import org.example.lab7.domain.FriendRequestStatus;
import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.User;
import org.example.lab7.events.ChangeEvent;
import org.example.lab7.events.ChangeEventType;
import org.example.lab7.service.Service;
import org.example.lab7.service.ServiceException;
import org.example.lab7.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UserController implements Observer<ChangeEvent> {
    Service service = Service.getInstance();

    @FXML
    TableView<User> tableView;

    @FXML
    TableColumn<User,String> tableColumnUsername;

    @FXML
    TableColumn<User,String> tableColumnFirstName;

    @FXML
    TableColumn<User,String> tableColumnLastName;

    @FXML
    TableColumn<User,String> tableColumnEmail;

    @FXML
    TableView<User> tableViewRequests;

    @FXML
    TableColumn<User,String> tableColumnUsernameR;

    @FXML
    TableColumn<User,String> tableColumnFirstNameR;

    @FXML
    TableColumn<User,String> tableColumnLastNameR;

    @FXML
    TableColumn<User,String> tableColumnEmailR;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldLastName;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldRequest;

    private User user;

    public UserController() {
    }

    public UserController(User user) {
        this.user = user;
    }

    public void setService(Service service, User user){
        this.service = service;
        this.user = user;
        initialize();
    }

    @FXML
    public void initialize(){
        if(user!=null){
            initTableFriends();
            tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

            initTableRequests();
            tableColumnUsernameR.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableColumnFirstNameR.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tableColumnLastNameR.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tableColumnEmailR.setCellValueFactory(new PropertyValueFactory<>("email"));

            textFieldFirstName.setText(user.getFirstName());
            textFieldLastName.setText(user.getLastName());
            textFieldUsername.setText(user.getId());
            textFieldEmail.setText(user.getEmail());
        }
    }

    @FXML
    private void initTableRequests() {
        Iterable<Optional<FriendRequest>> requests = StreamSupport.stream(service.getAllFriendRequests().spliterator(), false)
                .filter(r->r.get().getStatus().equals(FriendRequestStatus.PENDING))
                .collect(Collectors.toList());
        List<User> myRequests = StreamSupport.stream(requests.spliterator(), false)
                .filter(request -> request.get().getCatre().equals(user.getId()))
                .map(request -> service.findOne(request.get().getDela()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        tableViewRequests.getItems().addAll(myRequests);
    }

    @FXML
    public void initTableFriends() {
        Iterable<User> friends  = service.getFriendsOfUser(user.getId());
        List<User> friendsList = StreamSupport.stream(friends.spliterator(),false)
                .collect(Collectors.toList());
        tableView.getItems().addAll(friendsList);
    }

    @FXML
    public void handleLogOut(ActionEvent event){
        try {
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loginLoader = new FXMLLoader(StartApplication.class.getResource("login-controller.fxml"));
            stage.setTitle("Login/Sign-up Page");
            stage.setScene(new Scene(loginLoader.load()));
            LoginController loginController = loginLoader.getController();
            loginController.setService(service);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleUpdate(ActionEvent event) {
        try {
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            FXMLLoader updateLoader = new FXMLLoader(StartApplication.class.getResource("editUser-controller.fxml"));
            stage.setTitle("Update Page");
            stage.setScene(new Scene(updateLoader.load()));
            EditUserController updateController = updateLoader.getController();
            updateController.setService(service, user);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleDelete(ActionEvent event){
        try{
            service.removeUser(user.getId());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Remove user", "Account deleted successfully!");
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loginLoader = new FXMLLoader(StartApplication.class.getResource("login-controller.fxml"));
            stage.setTitle("Login/Sign-up Page");
            stage.setScene(new Scene(loginLoader.load()));
            LoginController loginController = loginLoader.getController();
            loginController.setService(service);
            stage.show();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void handleRequest(MouseEvent event){
        User selected = (User) tableViewRequests.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try {
                FriendRequest friendRequest = service.findOne(selected.getId(), user.getId(), FriendRequestStatus.PENDING).get();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(StartApplication.class.getResource("manageRequest-controller.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Manage Request");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(loader.load());
                dialogStage.setScene(scene);

                ManageRequestController manageRequestController = loader.getController();
                manageRequestController.setService(service, friendRequest);
                manageRequestController.setUserController(this);
                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSendRequest(ActionEvent event){
        Optional<User> newFriend = service.findOne(textFieldRequest.getText());
        if(newFriend.isPresent())
            try{
                service.addFriendRequest(user.getId(), newFriend.get().getId());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Request sent", "Request sent successfully!");
            } catch (ServiceException ex){
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        else{
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Request not sent", "Invalid username!");
        }
    }

    public void handleDeleteFriendship(MouseEvent event){
        User selected = (User) tableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try {
                Friendship friendship = service.findOne(user.getId(), selected.getId()).get();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(StartApplication.class.getResource("friendship-controller.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Remove friendship");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(loader.load());
                dialogStage.setScene(scene);
                FriendshipController friendshipController = loader.getController();
                friendshipController.setService(service, friendship);
                friendshipController.setUserController(this);
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleOpenChat(ActionEvent actionEvent){
        try {
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader chatLoader = new FXMLLoader(StartApplication.class.getResource("chatWindow-controller.fxml"));
            stage.setTitle("Chat Page");
            stage.setScene(new Scene(chatLoader.load()));
            ChatWindowController chatWindowController = chatLoader.getController();
            chatWindowController.setService(service, user);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(ChangeEvent data) {
        if(data.getType() == ChangeEventType.UPDATE){
            User user = (User) data.getData();
            textFieldFirstName.setText(user.getFirstName());
            textFieldLastName.setText(user.getLastName());
            textFieldUsername.setText(user.getId());
            textFieldEmail.setText(user.getEmail());
        }
        if (data.getType() == ChangeEventType.DELETE) {
            Friendship friendship = (Friendship) data.getData();
            tableView.getItems().removeIf(u -> u.getId().equals(friendship.getId().getFirst())
                || u.getId().equals(friendship.getId().getSecond()));
        }
        if(data.getType() == ChangeEventType.ACCEPT){
            User user = service.findOne((String) data.getData()).get();
            tableView.getItems().add(user);
            tableViewRequests.getItems().remove(user);
        }
        if(data.getType() == ChangeEventType.REJECT){
            User user = service.findOne((String) data.getData()).get();
            tableViewRequests.getItems().remove(user);
        }
    }
}
