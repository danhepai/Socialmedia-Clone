package org.example.mysocialnetworkgui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.service.UserService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ProfileController {
    Stage stage;
    UserService userService;
    Long userId;
    private final String picturesDirectory = "/Users/dangaspar/Documents/CS-UBB/Year02/Metode Avansate de Prog/Laboratoare/MySocialNetwork-GUI/src/main/java/org/example/mysocialnetworkgui/pictures/";

    @FXML
    public Text name;

    @FXML
    public ImageView profileImage;

    @FXML
    public Button uploadPic;

    public void initWindow(Stage stage, UserService userService, Long userId) {
        this.stage = stage;
        this.userService = userService;
        this.userId = userId;

        initModel();
    }

//    @FXML
//    public void initialize(){
//        name.setText(userService.getUserById(userId).getFirstName() + " " + userService.getUserById(userId).getLastName());
//        profileImage.setImage(new Image(userService.getPictureUrl(userId)));
//    }

    public void initModel(){
        name.setText(userService.getUserById(userId).getFirstName() + " " + userService.getUserById(userId).getLastName());

        File file = new File(picturesDirectory + userService.getPictureUrl(userId));
        String imageUrl = file.toURI().toString();
        Image image = new Image(imageUrl);
        profileImage.setImage(image);

    }

    public void uploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");

        // Filtrare pentru fișiere imagine
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Afișează FileChooser
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                // Copiază fișierul selectat în directorul specificat
                Path targetPath = Path.of(picturesDirectory + file.getName());
                Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Setează imaginea în ImageView
                String imageUrl = "file:" + targetPath.toString();
                profileImage.setImage(new Image(imageUrl));

//                // Actualizează calea în baza de date sau în serviciul utilizatorului
                userService.updateProfilePictureInDatabase(userId, file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                // Poți adăuga o alertă pentru a anunța utilizatorul că upload-ul a eșuat
            }
        }
    }
}
