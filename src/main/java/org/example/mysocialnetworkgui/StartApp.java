package org.example.mysocialnetworkgui;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

import org.example.mysocialnetworkgui.controllers.LoginController;
import org.example.mysocialnetworkgui.controllers.UserController;
import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.domain.validators.FriendshipValidator;
import org.example.mysocialnetworkgui.domain.validators.MessageValidator;
import org.example.mysocialnetworkgui.domain.validators.UserValidator;
import org.example.mysocialnetworkgui.repository.Repository;
import org.example.mysocialnetworkgui.repository.database.FriendshipDBRepository;
import org.example.mysocialnetworkgui.repository.database.MessageDBRepository;
import org.example.mysocialnetworkgui.repository.database.PagingRepository;
import org.example.mysocialnetworkgui.repository.database.UserDBRepository;
import org.example.mysocialnetworkgui.service.UserService;
import org.example.mysocialnetworkgui.service.FriendshipService;

public class StartApp extends Application {
    private UserService userService;
    private FriendshipService friendshipService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Dotenv dotenv = Dotenv.configure().directory("/Users/dangaspar/Documents/CS-UBB/Year02/Metode Avansate de Prog/Laboratoare/MySocialNetwork-GUI/src/main/java/org/example/mysocialnetworkgui/.env").load();
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USER");
        String pasword = dotenv.get("DB_PASSWORD");

        PagingRepository<Long, User> userRepo = new UserDBRepository(url, username, pasword, new UserValidator());
        Repository<Long, Friendship> friendshipRepo = new FriendshipDBRepository(url, username, pasword, new FriendshipValidator());
        Repository<Long, Message> messageRepo = new MessageDBRepository(url, username, pasword, new MessageValidator());
        userService = UserService.getInstance(userRepo, friendshipRepo);
        friendshipService = FriendshipService.getInstance(userRepo, friendshipRepo, messageRepo);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        LoginController loginController = fxmlLoader.getController();
        loginController.initWindow(primaryStage, userService, friendshipService);
    }
}