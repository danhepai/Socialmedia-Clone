package org.example.mysocialnetworkgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.mysocialnetworkgui.StartApp;
import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.service.FriendshipService;
import org.example.mysocialnetworkgui.service.UserService;
import org.example.mysocialnetworkgui.utils.events.UserEntityChangeEvent;
import org.example.mysocialnetworkgui.utils.observer.Observer;
import org.example.mysocialnetworkgui.utils.paging.Page;
import org.example.mysocialnetworkgui.utils.paging.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserEntityChangeEvent> {
    private UserService userService;
    private FriendshipService friendshipService;
    private final ObservableList<User> friendsModel = FXCollections.observableArrayList();
    private final ObservableList<User> availableUsersModel = FXCollections.observableArrayList();
    Stage stage;
    Long userID;

    private int pageSize = 5;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    @FXML
    public Label welcomeText;

    @FXML
    public TableView<User> friendsTable;
    @FXML
    public TableColumn<User, String> firstNameColumn;
    @FXML
    public TableColumn<User, String> lastNameColumn;

    @FXML
    public TableView<User> availableUsers;
    @FXML
    public TableColumn<User, String> firstNameAvailableUser;
    @FXML
    public TableColumn<User, String> lastNameAvailableUser;

    @FXML
    public Button requestFriendship;
    @FXML
    public Button unfriend;
    @FXML
    public Button profile;

    @FXML
    public Button myfriendRequests;
    @FXML
    public Button logOutButton;
    @FXML
    public Button conversationButton;
    @FXML
    public Button notificationsButton;
    @FXML
    public Button nextPageButton;
    @FXML
    public Button previousPageButton;
    @FXML
    public Label currentPageLabel;



    public void setService(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        userService.addObserver(this);
        initModel();
    }

    public void initWindow(Stage stage, UserService userService, FriendshipService friendshipService, Long userId) {
        this.stage = stage;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.userID = userId;
        initModel();
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameAvailableUser.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameAvailableUser.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        availableUsers.setItems(availableUsersModel);
        friendsTable.setItems(friendsModel);
    }

    private void initModel() {
        welcomeText.setText("Welcome, " + userService.getUserById(userID).getFirstName() + " " + userService.getUserById(userID).getLastName());

        Iterable<User> friends = friendshipService.getFriends(userID);
        List<User> friendsList = StreamSupport.stream(friends.spliterator(), false)
                                    .collect(Collectors.toList());
        friendsModel.setAll(friendsList);

//        Iterable<User> allPeople = userService.getAllUsers();
//        List<User> availablePeople = StreamSupport.stream(allPeople.spliterator(), false)
//                .filter(user -> !friendsList.contains(user))
//                .filter(user -> !user.getId().equals(userID))
//                .toList();
//        availableUsersModel.setAll(availablePeople);

        Page<User> page = userService.findAllUsersOnPage(new Pageable(currentPage, pageSize));
        totalNumberOfElements = page.getTotalNumberOfElements();
        int maxPage = (int) Math.ceil((double) totalNumberOfElements / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = userService.findAllUsersOnPage(new Pageable(currentPage, pageSize));
        }

        previousPageButton.setDisable(currentPage <= 0);
        nextPageButton.setDisable(currentPage >= maxPage);
        List<User> availableUsers = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .filter(user -> !friendsList.contains(user))
                .filter(user -> !user.getId().equals(userID))
                .toList();
        availableUsersModel.setAll(availableUsers);

        currentPageLabel.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    public void handleRequestFriendship(){
        requestFriendship.setOnAction(event -> {
            try {
                User selectedUser = availableUsers.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    friendshipService.addFriendship(userID, selectedUser.getId());
                    initModel();
                    MessageAlert.showInfoMessage(stage, "Friendship sent!");
                } else {
                    throw new Exception("Select a user!");
                }
            } catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    public void handleUnfriend() {
        unfriend.setOnAction(event -> {
            try {
                User selectedUser = friendsTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    friendshipService.deleteFriendship(userID, selectedUser.getId());
                    initModel();
                    MessageAlert.showInfoMessage(stage, "You unfriended " + selectedUser.getFirstName() + "!");
                } else {
                    throw new Exception("Select a user!");
                }
            } catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    public void handleProfile() {
        profile.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(StartApp.class.getResource("profile-view.fxml"));
                Stage windowStage = new Stage();

                AnchorPane userLayout = loader.load();
                windowStage.setScene(new Scene(userLayout));

                ProfileController profileController = loader.getController();
                profileController.initWindow(windowStage, userService, userID);

                windowStage.setTitle("Profile");
                windowStage.show();

            }
            catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void handleMyFriendRequests() {
        myfriendRequests.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(StartApp.class.getResource("friendRequests-view.fxml"));
                Stage windowStage = new Stage();

                AnchorPane userLayout = loader.load();
                windowStage.setScene(new Scene(userLayout, 630, 400));

                FriendRequestsController requestsController = loader.getController();
                requestsController.initWindow(windowStage, userService, friendshipService, userID, this);

                windowStage.setTitle("My Friend Requests");
                windowStage.show();
            }
            catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    public void updateFriendshipList(){
        initModel();
    }

    public void handleLogOut() {
        logOutButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(StartApp.class.getResource("login-view.fxml"));
                Stage windowStage = new Stage();

                AnchorPane userLayout = loader.load();
                windowStage.setScene(new Scene(userLayout));

                LoginController loginController = loader.getController();
                loginController.initWindow(windowStage, userService, friendshipService);

                windowStage.setTitle("Login");
                windowStage.show();
                stage.close();
            }
            catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    public void handleConversation() {
        conversationButton.setOnAction(event -> {
            try {
                User selectedUser = friendsTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    FXMLLoader loader = new FXMLLoader(StartApp.class.getResource("conversation-view.fxml"));
                    Stage windowStage = new Stage();

                    AnchorPane userLayout = loader.load();
                    windowStage.setScene(new Scene(userLayout));

                    ConversationController conversationController = loader.getController();
                    conversationController.initWindow(windowStage, userService, friendshipService, userID, selectedUser.getId());
                    windowStage.setTitle("Conversation with " + selectedUser.getFirstName() + " " + selectedUser.getLastName());
                    windowStage.show();
                } else {
                    throw new Exception("Select a user!");
                }

            }
            catch (Exception e) {
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }

    public void handleNotifications(){
        notificationsButton.setOnAction(event -> {
            Iterable<User> friendsPending = friendshipService.getPendingRequests(userID);
            List<User> usersListPending = StreamSupport.stream(friendsPending.spliterator(), false)
                    .toList();
            for (User user : usersListPending) {
                MessageAlert.showInfoMessage(stage, user.getFirstName() + " " + user.getLastName() + " send you a friend request!");
            }
        });
    }

    public void handlePreviousPage(){
        currentPage--;
        initModel();
    }

    public void handleNextPage(){
        currentPage++;
        initModel();
    }
}
