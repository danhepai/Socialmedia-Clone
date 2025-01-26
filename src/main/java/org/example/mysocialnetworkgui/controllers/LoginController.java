package org.example.mysocialnetworkgui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.mysocialnetworkgui.StartApp;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.repository.PasswordHasher;
import org.example.mysocialnetworkgui.service.FriendshipService;
import org.example.mysocialnetworkgui.service.UserService;

public class LoginController {
    Stage stage;
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private PasswordField password;

    UserService userService;
    FriendshipService friendshipService;

    public void initWindow(Stage stage, UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.stage = stage;
    }

    public void handleLogin() {
        try {
            User user = userService.getUserByFirstNameAndSecondName(firstName.getText(), lastName.getText());
            if (user == null) {
                throw new Exception("User not found. Create an account!");
            }
//            if (!password.getText().equals(user.getPassword())) {
//                throw new Exception("Wrong password. Try again!");
//            }
            if (!PasswordHasher.verifyPassword(password.getText(), user.getPassword())) {
                throw new Exception("Wrong password. Try again!");
            }

            FXMLLoader loader = new FXMLLoader(StartApp.class.getResource("userAccount-view.fxml"));
            Stage windowStage = new Stage();

            AnchorPane userLayout = loader.load();
            windowStage.setScene(new Scene(userLayout));

            UserController userController = loader.getController();
            userController.initWindow(windowStage, userService, friendshipService, user.getId());

            windowStage.setTitle("Account for " + user.getFirstName() + " " + user.getLastName());
            windowStage.show();
            stage.close();
        } catch (Exception e) {
            MessageAlert.showErrorMessage(stage, e.getMessage());
        }
    }

    public void handleSignUp() {
        try {
            if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || password.getText().isEmpty()) {
                throw new Exception("Please fill all fields!");
            }

            userService.addUser(firstName.getText(), lastName.getText(), password.getText());
            MessageAlert.showInfoMessage(stage, "Account created! Log in.");

        } catch (Exception e) {
            MessageAlert.showErrorMessage(stage, e.getMessage());
        }
    }
}
