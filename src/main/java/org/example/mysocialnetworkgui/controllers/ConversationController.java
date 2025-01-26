package org.example.mysocialnetworkgui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.service.FriendshipService;
import org.example.mysocialnetworkgui.service.UserService;
import org.example.mysocialnetworkgui.utils.events.UserEntityChangeEvent;
import org.example.mysocialnetworkgui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.example.mysocialnetworkgui.utils.Constants.DATE_TIME_FORMATTER;

public class ConversationController implements Observer<UserEntityChangeEvent> {
    private UserService userService;
    private FriendshipService friendshipService;

    private final ObservableList<String> messageModel = FXCollections.observableArrayList();
    Stage stage;
    Long userID;
    Long secondUserID;

    @FXML
    public ListView<String> messagesList;

    @FXML
    public TextField messageToSend;

    @FXML
    public Button sendMsgButton;

    public void initWindow(Stage stage, UserService userService, FriendshipService friendshipService, Long userId, Long secondUserId) {
        this.stage = stage;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.userID = userId;
        this.secondUserID = secondUserId;
        initModel();
    }

    @FXML
    public void initialize() {
        messagesList.setItems(messageModel);
    }

    private void initModel() {
        List<Message> messages = friendshipService.getConversation(userID, secondUserID);

        List<String> processedMessageList = messages.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .map(message -> String.format("%s, at %s: %s",
                        userService.getUserById(message.getFrom()).getFirstName(),
                        message.getDate().format(DATE_TIME_FORMATTER),
                        message.getMsg())
                ).toList();
        messageModel.setAll(processedMessageList);
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void handleSendMsg() {
        sendMsgButton.setOnAction(event -> {
            try {
                String message = messageToSend.getText();
                if (message == null)
                    throw new Exception("Message can't be null");
                friendshipService.sendMessage(userID, secondUserID, message, LocalDateTime.now());
                initModel();
            } catch (Exception e){
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        });
    }
}
