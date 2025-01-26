package org.example.mysocialnetworkgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.service.FriendshipService;
import org.example.mysocialnetworkgui.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController {
    @FXML
    public TableView<User> friendRequestsTable;
    @FXML
    public TableColumn<User, String> firstNameColumn;
    @FXML
    public TableColumn<User, String> lastNameColumn;
    @FXML
    public Button acceptButton;
    @FXML
    public Button declineButton;

    Stage stage;
    UserService userService;
    private FriendshipService friendshipService;
    private Long userID;
    private UserController parentController;

    private final ObservableList<User> requestsModel = FXCollections.observableArrayList();

    public void initWindow(Stage stage, UserService userService, FriendshipService friendshipService, Long userId, UserController parentController) {
        this.stage = stage;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.userID = userId;
        this.parentController = parentController;
        initModel();
    }

    private void initModel() {
        Iterable<User> friends = friendshipService.getPendingRequests(userID);
        List<User> usersList = StreamSupport.stream(friends.spliterator(), false)
                .collect(Collectors.toList());
        requestsModel.setAll(usersList);
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendRequestsTable.setItems(requestsModel);
    }

    public void handleAccept(){
        acceptButton.setOnAction(event -> {
            try {
                User selectedUser = friendRequestsTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    friendshipService.acceptFriendship(userID, selectedUser.getId(), 1);
                    initModel();
                    MessageAlert.showInfoMessage(stage, "Friendship accepted!");
                    parentController.updateFriendshipList();
                } else {
                    throw new Exception("Select a user!");
                }
            }
            catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    public void declineAccept(){
        declineButton.setOnAction(event -> {
            try {
                User selectedUser = friendRequestsTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    friendshipService.acceptFriendship(userID, selectedUser.getId(), 0);
                    initModel();
                    MessageAlert.showInfoMessage(stage, "Friendship declined!");
                    parentController.updateFriendshipList();
                } else {
                    throw new Exception("Select a user!");
                }
            }
            catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
        // BUG TO FIX: Decline - Send Again - Nu se vede din nou in Pending
    }

}
