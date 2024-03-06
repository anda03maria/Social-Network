package org.example.lab7.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.lab7.domain.FriendRequest;
import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.Message;
import org.example.lab7.domain.User;
import org.example.lab7.repository.paging.Page;
import org.example.lab7.repository.paging.Pageable;
import org.example.lab7.service.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PageController {

    @FXML
    private ListView<String> entitiesListView;

    private Service service = Service.getInstance();

    private int currentPageNumber = 1;

    private int pageSize;

    private String entity;

    @FXML
    public void setService(Service service, int pageSize, String entityText) {
        this.service = service;
        this.pageSize = pageSize;
        this.entity = entityText;
        service.setPageSize(pageSize);
        if(entityText.equals("user")){
            Page<User> users = service.getUsersPage(currentPageNumber);
            setUsers(users);
        } else if (entityText.equals("friendship")) {
            Page<Friendship> friendships = service.getFriendshipPage(currentPageNumber);
            setFriendships(friendships);
        } else if (entityText.equals("request")) {
            Page<FriendRequest> friendRequests = service.getFriendRequestPage(currentPageNumber);
            setFriendRequest(friendRequests);
        } else if (entityText.equals("message")) {
            setMessage(pageSize);
        }
    }

    private void setMessage(int pageSize) {
        List<Message> messageList = service.getLatestMessages(pageSize);
        entitiesListView.getItems().clear();
        for (Message message : messageList) {
            entitiesListView.getItems().add(message.getFrom() + " to " + message.getTo() + ": " + message.getMessage() + " " + message.getDate());
        }
    }


    public void setUsers(Page<User> usersPage){
        //Page<User> users = service.getUsersPage(currentPageNumber);
        List<User> userList = usersPage.getContent().collect(Collectors.toList());
        entitiesListView.getItems().clear();
        for (User user : userList) {
            entitiesListView.getItems().add(user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());
        }
    }

    public void setFriendships(Page<Friendship> friendshipPage){
        //Page<Friendship> friendships = service.getFriendshipPage(currentPageNumber);
        List<Friendship> friendshipList = friendshipPage.getContent().collect(Collectors.toList());
        entitiesListView.getItems().clear();
        for (Friendship friendship : friendshipList) {
            entitiesListView.getItems().add(friendship.getId().getFirst() + " " + friendship.getId().getSecond() + " " + friendship.getFriendsFrom());
        }
    }

    private void setFriendRequest(Page<FriendRequest> friendRequestPage) {
        //Page<FriendRequest> friendRequests = service.getFriendRequestPage(currentPageNumber);
        List<FriendRequest> friendRequestList = friendRequestPage.getContent().collect(Collectors.toList());
        entitiesListView.getItems().clear();
        for(FriendRequest friendRequest: friendRequestList){
            entitiesListView.getItems().add(friendRequest.getDela() + " " + friendRequest.getCatre() + " " + friendRequest.getStatus());
        }
    }

    @FXML
    public void handleNext(ActionEvent actionEvent){
        if(entity.equals("user")){
            Pageable nextPageable = service.getUsersPage(currentPageNumber).nextPageable();
            Page<User> nextPage = service.getUsersPage(nextPageable.getPageNumber());
            setUsers(nextPage);
        }
        else if (entity.equals("friendship")){
            Pageable nextPageable = service.getFriendshipPage(currentPageNumber).nextPageable();
            Page<Friendship> nextPage = service.getFriendshipPage(nextPageable.getPageNumber());
            setFriendships(nextPage);
        }
        else if (entity.equals("request")){
            Pageable nextPageable = service.getFriendRequestPage(currentPageNumber).nextPageable();
            Page<FriendRequest> nextPage = service.getFriendRequestPage(nextPageable.getPageNumber());
            setFriendRequest(nextPage);
        }
        currentPageNumber++;
    }

    @FXML
    public void handlePrevious(ActionEvent actionEvent){
        if (currentPageNumber > 1) {
            if(entity.equals("user")){
                Pageable previousPageable = service.getUsersPage(currentPageNumber).previousPageable();
                Page<User> previousPage = service.getUsersPage(previousPageable.getPageNumber());
                setUsers(previousPage);
            }
            else if (entity.equals("friendship")){
                Pageable previousPageable = service.getFriendshipPage(currentPageNumber).previousPageable();
                Page<Friendship> previousPage = service.getFriendshipPage(previousPageable.getPageNumber());
                setFriendships(previousPage);
            }
            else if (entity.equals("request")){
                Pageable previousPageable = service.getFriendRequestPage(currentPageNumber).previousPageable();
                Page<FriendRequest> previousPage = service.getFriendRequestPage(previousPageable.getPageNumber());
                setFriendRequest(previousPage);
            }
            currentPageNumber--;
        }
    }
}
