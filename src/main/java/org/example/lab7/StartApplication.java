package org.example.lab7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;
import org.example.lab7.controller.LoginController;
import org.example.lab7.controller.UserController;
import org.example.lab7.domain.Friendship;
import org.example.lab7.domain.StringPair;
import org.example.lab7.domain.User;
import org.example.lab7.repository.Repository;
import org.example.lab7.repository.db.FriendshipDBRepository;
import org.example.lab7.repository.db.UserDBRepository;
import org.example.lab7.service.Service;
import org.example.lab7.validator.FriendshipValidator;
import org.example.lab7.validator.UserValidator;
import org.example.lab7.validator.Validator;

import java.io.IOException;

public class StartApplication extends Application {
    Validator<User> userValid = new UserValidator();
    Validator<Friendship> friendshipValid = new FriendshipValidator();
    Repository<String, User> usersRepo = new UserDBRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "anda", userValid);
    Repository<StringPair, Friendship> friendshipsRepo = new FriendshipDBRepository("jdbc:postgresql://localhost:5433/socialnetwork","postgres", "anda", friendshipValid);
    private Service service = Service.getInstance();

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(StartApplication.class.getResource("login-controller.fxml"));

        primaryStage.setTitle("Login/Sign-up Page");
        primaryStage.setScene(new Scene(loginLoader.load()));

        LoginController loginController = loginLoader.getController();
        loginController.setService(service);
    }
}
