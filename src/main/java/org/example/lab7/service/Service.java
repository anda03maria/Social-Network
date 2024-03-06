package org.example.lab7.service;


import org.example.lab7.domain.*;
import org.example.lab7.events.ChangeEvent;
import org.example.lab7.repository.Repository;
import org.example.lab7.repository.db.*;
import org.example.lab7.repository.paging.*;
import org.example.lab7.utils.observer.Observable;
import org.example.lab7.utils.observer.Observer;
import org.example.lab7.validator.FriendRequestValidator;
import org.example.lab7.validator.FriendshipValidator;
import org.example.lab7.validator.UserValidator;
import org.example.lab7.validator.Validator;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.example.lab7.events.ChangeEventType.*;

/**
 * Service used for managing the SocialNetwork
 */
public class Service implements Observable<ChangeEvent> {

    private static Service instance = null;

    private Validator<User> validatorUser;
    private Validator<Friendship> validatorFriendship;
    private Validator<FriendRequest> validatorRequest;
    private Repository<String, User> usersRepo;
    private Repository<StringPair, Friendship> friendshipsRepo;
    private Repository<Long, FriendRequest> friendRequestRepo;

    private Repository<Integer, Message> messageRepo;
    private Repository<Integer, Conversation> conversationRepo;

    private PagingRepository<String, User> usersRepoPage;
    private PagingRepository<StringPair, Friendship> friendshipsRepoPage;
    private PagingRepository<Long, FriendRequest> requestsRepoPage;
    private int pageSize;

    private Service(UserValidator userValidator, FriendshipValidator friendshipValidator, FriendRequestValidator validatorRequest, Repository<String, User> usersRepo, Repository<StringPair, Friendship> friendshipsRepo, Repository<Long, FriendRequest> friendRequestRepo,
                    Repository<Integer, Message> messageRepo, Repository<Integer, Conversation> conversationRepo, PagingRepository<String, User> usersRepoPage,
                    PagingRepository<StringPair, Friendship> friendshipsRepoPage, PagingRepository<Long, FriendRequest> requestsRepoPage) {
        this.validatorUser = new UserValidator();
        this.validatorFriendship = new FriendshipValidator();
        this.validatorRequest = new FriendRequestValidator();
        this.usersRepo = usersRepo;
        this.friendshipsRepo = friendshipsRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.messageRepo = messageRepo;
        this.conversationRepo = conversationRepo;
        this.usersRepoPage = usersRepoPage;
        this.friendshipsRepoPage = friendshipsRepoPage;
        this.requestsRepoPage = requestsRepoPage;
    }

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service(new UserValidator(), new FriendshipValidator(), new FriendRequestValidator(),
                    new UserDBRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", new UserValidator()),
                    new FriendshipDBRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", new FriendshipValidator()),
                    new FriendRequestDBRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", new FriendRequestValidator()),
                    new MessageDBRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda"),
                    new ConversationDBRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda"),
                    new UserDBPagingRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", new UserValidator()),
                    new FriendshipDBPagingRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", new FriendshipValidator()),
                    new FriendRequestDBPagingRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", new FriendRequestValidator())
            );}
        return instance;
    }


    /**
     * Adds a new user
     *
     * @param firstName, String
     * @param lastName,  String
     * @param userName,  String
     */
    public void addUser(String userName, String firstName, String lastName, String email, String password) throws NoSuchAlgorithmException {
        String encryptedPassword = StringHash.toHexString(StringHash.getSHA(password));
        User newUser = new User(userName, firstName, lastName, email, encryptedPassword);
        validatorUser.validate(Optional.of(newUser));
        if (!usersRepo.save(newUser)) {
            throw new ServiceException("User could not be saved!");
        }
        notifyObservers(new ChangeEvent(ADD,newUser));

    }

    /**
     * Removes a user and all their friendships
     *
     * @param userName, String
     */
    public void removeUser(String userName) {
        Optional<User> user = usersRepo.findOne(userName);
        user
                .ifPresentOrElse(u -> {
                            usersRepo.delete(userName);
                        },
                        () -> {
                            throw new ServiceException("User does not exist!");

                        })
        ;
        notifyObservers(new ChangeEvent(DELETE,user.get()));
    }

    /**
     * Updates an existing user, or throws error is user is not found
     *
     * @param userName  id
     * @param newParam  new value
     * @param parameter the field which is updated
     */
    public void updateUser(String userName, String newParam, UserParameter parameter) {
        Optional<User> userOptional = usersRepo.findOne(userName);
        if (userOptional == null) {
            throw new ServiceException("User not found!");
        }

        User user = userOptional.get();
        User oldUserData = new User(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());

        switch (parameter) {
            case FIRST_NAME:
                oldUserData.setFirstName(user.getFirstName());
                user.setFirstName(newParam);
                break;
            case LAST_NAME:
                oldUserData.setLastName(user.getLastName());
                user.setLastName(newParam);
                break;
            case EMAIL:
                oldUserData.setEmail(user.getEmail());
                user.setEmail(newParam);
                break;
            default:
                throw new ServiceException("Invalid user parameter!");
        }

        if (!usersRepo.update(user)) {
            throw new ServiceException("User could not be updated!");
        }

        notifyObservers(new ChangeEvent(UPDATE, user, oldUserData));
    }

    /**
     * @return The list with all the Users
     */
    public Iterable<Optional<User>> getAllUsers() {
        return usersRepo.findAll();
    }

    /**
     * Adds a new friendship
     *
     * @param userName1, String
     * @param userName2, String
     */
    public void addFriendShip(String userName1, String userName2) {
        Optional<User> user1 = usersRepo.findOne(userName1);
        Optional<User> user2 = usersRepo.findOne(userName2);
        Friendship friendship = new Friendship(userName1, userName2);
        validatorFriendship.validate(Optional.of(friendship));
        if (user1.isEmpty() || user2.isEmpty()) {
            throw new ServiceException("Unable to create friendship! At least one user does not exist!");
        }
        if (!friendshipsRepo.save(friendship)) {
            throw new ServiceException("Failed to save friendship! It already exists!");
        }
    }

    /**
     * Removes a friendship
     *
     * @param userName1, String
     * @param userName2, String
     */
    public void removeFriendship(String userName1, String userName2) {
        Optional<Friendship> friendshipOptional = findOne(userName1, userName2);
        if (friendshipOptional.isEmpty()) {
            throw new ServiceException("Friendship does not exist!");
        }
        Friendship friendship = friendshipOptional.get();
        validatorFriendship.validate(Optional.of(friendship));
        boolean friendshipDeleted = friendshipsRepo.delete(new StringPair(friendship.getId().getFirst(), friendship.getId().getSecond())).isPresent();
        if (!friendshipDeleted) {
            throw new ServiceException("Failed to delete friendship!");
        }
        notifyObservers(new ChangeEvent(DELETE,friendship));
    }

    /**
     * Updates a friendship entity or throws error is it is not found
     *
     * @param userName1 first ID
     * @param userName2 second ID
     */
    public void updateFriendship(String userName1, String userName2) {
        Optional<Friendship> friendshipOptional = Optional.of(new Friendship(userName1, userName2));
        validatorFriendship.validate(friendshipOptional);
        friendshipOptional = friendshipsRepo.findOne(new StringPair(userName1, userName2));
        if (friendshipOptional == null) {
            throw new ServiceException("Friendship does not exist!");
        }
        friendshipOptional.ifPresent(friendship -> {
            friendship.setFriendsFrom(LocalDateTime.now());
            friendshipsRepo.update(friendship);
        });
    }



    /**
     * @return A list with all the friendships
     */
    public Iterable<Optional<Friendship>> getAllFriendships() {
        return friendshipsRepo.findAll();
    }

    /**
     * @return All communities from the community graph
     */
    public List<List<Optional<User>>> getAllCommunities() {
        CommunityService communityManager = new CommunityService(getAllUsersWithFriends());
        return communityManager.getAllCommunities();
    }

    /**
     * @return The community with the longest path
     */
    public List<Optional<User>> getMostSociableCommunity() {
        CommunityService communityService = new CommunityService(getAllUsersWithFriends());
        return communityService.findMostSociableCommunity();
    }

    /**
     * Builds a list with CommunityNodes, where every community node
     * represents a particular user and all of their friends
     *
     * @return List with all CommunityNodes created from the saved users and saved friendships
     */
    private List<CommunityNode> getAllUsersWithFriends() {
        List<CommunityNode> nodes = new ArrayList<>();
        StreamSupport.stream(usersRepo.findAll().spliterator(), false)
                .map(Optional::get)
                .map(user -> getUserWithFriends(user.getId()))
                .filter(Objects::nonNull)
                .forEach(nodes::add);
        return nodes;
    }

    /**
     * For a particular user, it builds a CommunityNode which consists of that user
     * and all of their friends
     *
     * @param userId, String
     * @return CommunityNode made from user, of null is userId does not exist
     */
    private CommunityNode getUserWithFriends(String userId) {
        Optional<User> user = usersRepo.findOne(userId);
        if (user.isEmpty()) {
            return null;
        }
        CommunityNode communityNode = new CommunityNode(Optional.of(user.get()));
        Iterable<Optional<Friendship>> friendships = friendshipsRepo.findAll();
        friendships.forEach(friendship -> {
            String userName1 = friendship.get().getId().getFirst();
            String userName2 = friendship.get().getId().getSecond();
            if (userName1.equals(userId)) {
                communityNode.addFriend(usersRepo.findOne(userName2));
            } else if (userName2.equals(userId)) {
                communityNode.addFriend(usersRepo.findOne(userName1));
            }
        });
        return communityNode;
    }

    /**
     * For a particular user, it returns all the friendships of that user from a particular
     * month
     *
     * @param username, String
     * @param month, String
     */
    public Iterable<Optional<Friendship>> getFriendshipsFromMonth(String username, String month) {
        return StreamSupport.stream(friendshipsRepo.findAll().spliterator(), false)
                .filter(friendship -> friendship.get().getFriendsFrom().getMonthValue() == Integer.parseInt(month) &&
                        (friendship.get().getId().getFirst().equals(username) ||
                                friendship.get().getId().getSecond().equals(username)))
                .collect(Collectors.toList());
    }

    public Iterable<User> getFriendsOfUser(String username){
        Set<String> friendUsernames = StreamSupport.stream(friendshipsRepo.findAll().spliterator(), false)
                .filter(friendship -> friendship.get().getId().getFirst().equals(username) ||
                        friendship.get().getId().getSecond().equals(username))
                .flatMap(friendship -> Stream.of(friendship.get().getId().getFirst(), friendship.get().getId().getSecond()))
                .collect(Collectors.toSet());

        return friendUsernames.stream()
                .filter(friendUsername -> !friendUsername.equals(username))
                .map(usersRepo::findOne)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<User> findOne(String username){
        return usersRepo.findOne(username);
    }

    public Optional<Friendship> findOne(String username1, String username2){
        return friendshipsRepo.findOne(new StringPair(username1, username2));
    }

    /**
     * Adds a new friend request
     *
     * @param userName1, String
     * @param userName2, String
     */
    public void addFriendRequest(String userName1, String userName2) {
        Optional<User> user1 = usersRepo.findOne(userName1);
        Optional<User> user2 = usersRepo.findOne(userName2);
        FriendRequest friendRequest = new FriendRequest(userName1, userName2, FriendRequestStatus.PENDING);
        validatorRequest.validate(Optional.of(friendRequest));
        if (user1.isEmpty() || user2.isEmpty()) {
            throw new ServiceException("Unable to send request! At least one user does not exist!");
        }
        if (!friendRequestRepo.save(friendRequest)) {
            throw new ServiceException("Failed to send request! It already exists!");
        }
    }

    public void updateFriendRequest(Long id, FriendRequestStatus status) {
        Optional<FriendRequest> friendRequest = friendRequestRepo.findOne(id);
        validatorRequest.validate(friendRequest);
        if (friendRequest == null) {
            throw new ServiceException("Friend request does not exist!");
        }
        friendRequest.ifPresent(request -> {
            request.setStatus(status);
            friendRequestRepo.update(request);
        });
    }

    /**
     * @return A list with all the friend requests
     */
    public Iterable<Optional<FriendRequest>> getAllFriendRequests() {
        return friendRequestRepo.findAll();
    }

    public Optional<FriendRequest> findOne(String from, String to, FriendRequestStatus status){
        Optional<Optional<FriendRequest>> fr = StreamSupport.stream(getAllFriendRequests().spliterator(),false)
                .filter(request-> {
                    String dela = request.get().getDela();
                    String catre = request.get().getCatre();
                    FriendRequestStatus s = request.get().getStatus();
                    return dela.equals(from) && catre.equals(to) && s.equals(status);
                })
                .findFirst();
        return fr.get();
    }

    private List<Observer<ChangeEvent>> observers = new ArrayList<>();

    @Override
    public void notifyObservers(ChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) {
        observers.remove(e);

    }

    private static User currentLoggedUser;

    public static User getCurrentLoggedUser() {
        return currentLoggedUser;
    }
    public void setCurrentLoggedUser(User user) {
        currentLoggedUser = user;
    }

    public void addMessage(int idConversation, String sentFrom, String sentTo, String text) {
        Message newMessage = new Message(idConversation, sentFrom, sentTo, text,LocalDateTime.now());
        if (!messageRepo.save(newMessage)) {
            throw new ServiceException("Message could not be sent!");
        }
        notifyObservers(new ChangeEvent(ADD, newMessage));
    }

    /**
     * Creates a new conversation entity
     * It first checks that there are no other entities with these fields
     * Since the primary key is not formed by the 2 IDs, the generic function
     * from repository can't help, so we have to manually check
     * @param firstUser
     * @param secondUser
     */
    public void addConversation(String firstUser, String secondUser) {
        Conversation conversation = new Conversation(firstUser, secondUser);
        List<Conversation> possibleConversations = StreamSupport.stream(conversationRepo.getCustomList(String.format("select * from conversation " +
                        "where first_user = '%s' and second_user = '%s'", conversation.getFirstUser(), conversation.getSecondUser())).spliterator(), false)
                .collect(Collectors.toList());
        if (possibleConversations.size() != 0) {
            throw new ServiceException("You already have a conversation with this user!");
        }
        conversationRepo.save(conversation);
        notifyObservers(new ChangeEvent(ADD, conversation));
    }

    /**
     * Searches for a conversation based on the 2 ID's
     * @param firstUser
     * @param secondUser
     */
    public Conversation findConversation(String firstUser, String secondUser) {
        StringPair orderedStringPair = new StringPair(firstUser, secondUser);
        Iterable<Conversation> resultSet = conversationRepo.getCustomList(String.format("select * from conversation where " +
                "first_user = '%s' and second_user = '%s'", orderedStringPair.getFirst(), orderedStringPair.getSecond()));
        if (resultSet.iterator().hasNext()) {
            return resultSet.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Collects all the messages from the conversation with the given ID
     * @param conversationID id of the conversation we want to get all the messages from
     * @return the list of messages
     */
    public Iterable<Message> getMessagesFromConversation(int conversationID) {
        Iterable<Message> messagesFromConversation = messageRepo.getCustomList(String.format("select * from messages where id_conversation = %d", conversationID));
        return StreamSupport.stream(messagesFromConversation.spliterator(), false)
                .sorted(new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                }).collect(Collectors.toList());
    }

    private Iterable<Optional<Message>> getAllMessages() {
        return messageRepo.findAll();
    }

    public Page<User> getUsersPage(int pageNumber){
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        Stream<User> users = usersRepoPage.findAll(pageable).getContent();
        return new PageImplementation<>(pageable, users);
    }

    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    public Page<Friendship> getFriendshipPage(int pageNumber){
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        Stream<Friendship> friendships = friendshipsRepoPage.findAll(pageable).getContent();
        return new PageImplementation<>(pageable, friendships);
    }

    public Page<FriendRequest> getFriendRequestPage(int pageNumber){
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        Stream<FriendRequest> friendRequests =  requestsRepoPage.findAll(pageable).getContent();
        return new PageImplementation<>(pageable, friendRequests);
    }

    public List<Message> getLatestMessages(int nr) {
//        Iterable<Optional<Message>> allMessages = getAllMessages();
//        List<Message> messages = new ArrayList<Message>();
//        for(Optional<Message> message : allMessages){
//            messages.add(message.get());
//        }
//        int startIndex = messages.size() - nr;
//        List<Message> latestMessages = messages.subList(startIndex, messages.size());
//        return latestMessages;

        Iterable<Optional<Message>> allMessages = getAllMessages();
        List<Message> messages = new ArrayList<Message>();
        for(Optional<Message> message : allMessages){
            messages.add(message.get());
        }
        return messages;
    }
}
