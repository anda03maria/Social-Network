package org.example.lab7.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.lab7.StartApplication;
import org.example.lab7.domain.*;
import org.example.lab7.events.ChangeEvent;
import org.example.lab7.service.Service;
import org.example.lab7.service.ServiceException;
import org.example.lab7.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatWindowController implements Observer<ChangeEvent> {
    @FXML
    private ListView<String> conversationListView;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private TextField usersTextField;

    @FXML
    private TableView<User> tableViewFriends;

    @FXML
    TableColumn<User,String> tableColumnUsername;

    @FXML
    TableColumn<User,String> tableColumnFirstName;

    @FXML
    TableColumn<User,String> tableColumnLastName;

    @FXML
    TableColumn<User,String> tableColumnEmail;

    private Service service = Service.getInstance();
    private User user;

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

            messageTextArea.setText("");
            usersTextField.setText("");
        }
    }

    @FXML
    public void initTableFriends() {
        Iterable<User> friends  = service.getFriendsOfUser(user.getId());
        List<User> friendsList = StreamSupport.stream(friends.spliterator(),false)
                .collect(Collectors.toList());
        tableViewFriends.getItems().addAll(friendsList);
    }

    @FXML
    private void handleClose(ActionEvent actionEvent){
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("user-controller.fxml"));
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
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
    private void handleSendMessage(ActionEvent actionEvent) {
        String messageText = messageTextArea.getText();
        String usersText = usersTextField.getText();
        String[] usernames = usersText.split(",");
        try {
            for (String username : usernames) {
                username = username.trim();
                User receiver = service.findOne(username).orElse(null);
                Optional<Friendship> friendship = service.findOne(username, user.getId());
                if(friendship.isEmpty()){
                    MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Invalid user!", "You are not friends! You cannot message this user!");
                }
                else {
                    if (receiver != null) {
                        Conversation conversation = service.findConversation(user.getId(), receiver.getId());
                        if (conversation == null) {
                            service.addConversation(user.getId(), receiver.getId());
                            conversation = service.findConversation(user.getId(), receiver.getId());
                        }
                        service.addMessage(conversation.getId(), user.getId(), receiver.getId(), messageText);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Message sent!", "Message sent successfully!");
                    } else {
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Invalid user!", "Invalid username!");
                    }
                }
            }
        }catch (ServiceException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleShowConversation(MouseEvent event){
        User selectedUser = (User) tableViewFriends.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            service.setCurrentLoggedUser(user);
            String currentUserID = Service.getCurrentLoggedUser().getId();
            Conversation conversation = service.findConversation(selectedUser.getId(), currentUserID);
            if(conversation==null) MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Conversation", "No messages yet!");
            else {
                Iterable<Message> messages = service.getMessagesFromConversation(conversation.getId());
                conversationListView.getItems().clear();
                for (Message message : messages) {
                    conversationListView.getItems().add(message.getFrom() + ": " + message.getMessage());
                }
            }
        }
    }

    @FXML
    public void handleReply(MouseEvent event){
        int index = conversationListView.getSelectionModel().getSelectedIndex();
        String selectedMessage = conversationListView.getItems().get(index);
        String[] splitted = selectedMessage.split(": ");
        String username = splitted[0];
        Optional<User> selectedUser = service.findOne(username);
        String messageContent = splitted[1];

        if (selectedUser.isPresent()){
            Conversation conversation = service.findConversation(username, user.getId());
            if(conversation!=null) {
                int conversationID = conversation.getId();
                Message originalMessage = new Message(conversationID, username, user.getId(), messageContent, LocalDateTime.now());

                String messageText = "[" + selectedMessage + "] " + messageTextArea.getText();
                usersTextField.setText(username);

                ReplyMessage replyMessage = new ReplyMessage(originalMessage.getId(), conversationID, user.getId(), username, messageText,
                        LocalDateTime.now(), originalMessage);
                service.addMessage(replyMessage.getIdConversation(), replyMessage.getFrom(), replyMessage.getTo(), replyMessage.getMessage());

                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Reply sent!", "Reply sent successfully!");
            }
        }
        else {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Invalid user!", "Invalid username!");
        }
    }

    @Override
    public void update(ChangeEvent changeEvent) {

    }
}
