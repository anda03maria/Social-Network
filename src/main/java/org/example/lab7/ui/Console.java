//package org.example.lab7.ui;
//
//
//import org.example.lab7.domain.Friendship;
//import org.example.lab7.domain.User;
//import org.example.lab7.domain.UserParameter;
//import org.example.lab7.service.Service;
//import org.example.lab7.service.ServiceException;
//import org.example.lab7.validator.ValidationException;
//
//import java.util.*;
//
//public class Console {
//    private final Scanner scanner;
//    private Service service;
//
//    public Console(Service service) {
//        this.service = service;
//        scanner = new Scanner(System.in);
//    }
//
//    private void printOptions() {
//        System.out.println("----------- MENU OPTIONS -----------");
//        System.out.println("->  0 | Exit");
//        System.out.println("->  1 | Add new user");
//        System.out.println("->  2 | Remove user");
//        System.out.println("->  3 | Update user");
//        System.out.println("->  4 | Get all users");
//        System.out.println("->  5 | Add new friendship");
//        System.out.println("->  6 | Remove friendship");
//        System.out.println("->  7 | Update friendship");
//        System.out.println("->  8 | Get all friendships");
//        System.out.println("->  9 | Print all communities");
//        System.out.println("-> 10 | Find most sociable community");
//        System.out.println("-> 11 | All friendships for a user from a month");
//        System.out.println();
//    }
//
//    public void run() {
//        boolean stillRunning = true;
//        printOptions();
//        while (stillRunning) {
//            System.out.print(">>>");
//            String option = scanner.nextLine();
//            switch (option) {
//                case "menu" -> printOptions();
//                case "0" -> stillRunning = false;
//                case "1" -> addUser();
//                case "2" -> removeUser();
//                case "3" -> updateUser();
//                case "4" -> printAllUsers();
//                case "5" -> addFriendShip();
//                case "6" -> removeFriendship();
//                case "7" -> updateFriendship();
//                case "8" -> printAllFriendships();
//                case "9" -> printAllCommunities();
//                case "10" -> printMostSociableCommunity();
//                case "11" -> printAllFriendshipsFromMonth();
//                default -> System.out.println("Invalid option!");
//            }
//        }
//    }
//
//    private void addUser() {
//        String firstName, lastName, email, userName;
//        System.out.print("Enter first name: ");
//        firstName = scanner.nextLine();
//        System.out.print("Enter last name: ");
//        lastName = scanner.nextLine();
//        System.out.print("Enter email: ");
//        email = scanner.nextLine();
//        System.out.print("Enter user name: ");
//        userName = scanner.nextLine();
//        System.out.println();
//        try{
//            service.addUser(userName, firstName, lastName, email);
//            System.out.println("User added successfully");
//        } catch(ServiceException exception){
//            System.out.println(exception.getMessage());
//        }
//        System.out.println();
//    }
//
//    private void removeUser() {
//        String userName;
//        System.out.print("Enter user name: ");
//        userName = scanner.nextLine();
//        try {
//            service.removeUser(userName);
//            System.out.println("User deleted successfully");
//        } catch (ServiceException exception) {
//            System.out.println(exception.getMessage());
//        }
//        System.out.println();
//    }
//
//    private void updateUser() {
//        System.out.print("User Name: ");
//        String userName = scanner.nextLine();
//        Map<String, UserParameter> updateOptions = new HashMap<>();
//        updateOptions.put("1", UserParameter.FIRST_NAME);
//        updateOptions.put("2", UserParameter.LAST_NAME);
//        updateOptions.put("3", UserParameter.EMAIL);
//        System.out.println("-> 1 | Update first name");
//        System.out.println("-> 2 | Update last name");
//        System.out.println("-> 3 | Update email");
//        System.out.println();
//        System.out.print(">>>");
//        String option = scanner.nextLine();
//        System.out.print("Enter new value: ");
//        String newValue = scanner.nextLine();
//        if (!updateOptions.containsKey(option)) {
//            System.out.println("Invalid option!\n");
//        }
//        try {
//            service.updateUser(userName, newValue, updateOptions.get(option));
//            System.out.println("User has been updated!");
//        } catch (ServiceException | ValidationException exception) {
//            System.out.println(exception.getMessage());
//        }
//        System.out.println();
//    }
//
//    private void printAllUsers() {
//        Iterable<Optional<User>> users = service.getAllUsers();
//        for (Optional<User> user : users) {
//            System.out.println(user);
//        }
//        System.out.println();
//    }
//
//    private void addFriendShip() {
//        System.out.print("Enter the 2 user names: ");
//        String lineArg = scanner.nextLine();
//        String[] args = lineArg.split(" ");
//        if (args.length != 2) {
//            System.out.println("Invalid input!");
//        } else {
//            try {
//                service.addFriendShip(args[0], args[1]);
//                System.out.println("Friendship added successfully!");
//            } catch (ServiceException | ValidationException exception) {
//                System.out.println(exception.getMessage());
//            }
//        }
//        System.out.println();
//    }
//
//    private void printAllFriendships() {
//        Iterable<Optional<Friendship>> friendships = service.getAllFriendships();
//        for (Optional<Friendship> friendship : friendships) {
//            System.out.println(friendship);
//        }
//        System.out.println();
//    }
//
//    private void removeFriendship() {
//        System.out.print("Enter the 2 user names: ");
//        String lineArg = scanner.nextLine();
//        String[] args = lineArg.split(" ");
//        if (args.length != 2) {
//            System.out.println("Invalid input!");
//        } else {
//            try {
//                service.removeFriendship(args[0], args[1]);
//                System.out.println("Friendship removed successfully");
//            } catch (ServiceException exception) {
//                System.out.println(exception.getMessage());
//            }
//        }
//        System.out.println();
//    }
//
//    private void updateFriendship() {
//        System.out.print("Enter the 2 user names: ");
//        String lineArg = scanner.nextLine();
//        String[] args = lineArg.split(" ");
//        if (args.length != 2) {
//            System.out.println("Invalid input!");
//        } else {
//            try {
//                service.updateFriendship(args[0], args[1]);
//                System.out.println("Friendship updated! Starting date restarted!");
//            } catch (ServiceException exception) {
//                System.out.println(exception.getMessage());
//            }
//        }
//        System.out.println();
//    }
//
//    private void printAllCommunities() {
//        List<List<Optional<User>>> communities = service.getAllCommunities();
//        System.out.printf("There are %d communities in our app:%n", communities.size());
//        int index = 1;
//        for (List<Optional<User>> community : communities) {
//            System.out.printf("Community #%d%n", index++);
//            for (Optional<User> user : community) {
//                System.out.printf("%s ", user.get().getId());
//            }
//            System.out.println();
//        }
//        System.out.println();
//    }
//
//    private void printMostSociableCommunity() {
//        List<Optional<User>> mostSociableCommunity = service.getMostSociableCommunity();
//        if (mostSociableCommunity == null) {
//            System.out.println("There is no community yet!");
//        } else {
//            System.out.print("Most sociable community: ");
//            for (Optional<User> user : mostSociableCommunity) {
//                System.out.printf("%s ", user.get().getId());
//            }
//            System.out.println();
//        }
//        System.out.println();
//    }
//
//    private void printAllFriendshipsFromMonth() {
//        System.out.println("Enter username: ");
//        String username = scanner.nextLine();
//        System.out.println("Enter month: ");
//        String month = scanner.nextLine();
//
//            try {
//                Iterable<Optional<Friendship>> friendships = service.getFriendshipsFromMonth(username, month);
//                if(!friendships.iterator().hasNext()){
//                    System.out.println("There are no frienships for this user made in this month.");
//                }
//                System.out.println("Nume prieten | Prenume prieten | Data de la care sunt prieteni");
//
//                for (Optional<Friendship> friendship : friendships) {
//                    if(friendship.get().getId().getFirst().equals(username)){
//                        Optional<User> user  = service.findOne(friendship.get().getId().getSecond());
//                        System.out.println(user.get().getLastName()+" | "+user.get().getFirstName()+" | "+friendship.get().getFriendsFrom()+"\n");
//                    }
//                    else{
//                        Optional<User> user  = service.findOne(friendship.get().getId().getFirst());
//                        System.out.println(user.get().getLastName()+" | "+user.get().getFirstName()+" | "+friendship.get().getFriendsFrom()+"\n");
//                    }
//
//                }
//            } catch (ServiceException | ValidationException exception) {
//                System.out.println(exception.getMessage());
//            }
//        System.out.println();
//    }
//}
