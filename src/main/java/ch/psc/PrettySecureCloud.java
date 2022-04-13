package ch.psc;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.user.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class PrettySecureCloud extends Application {
//    private User user;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Optional<User> user = loadUser();
//        storageManager = new StorageManager(loadUser());
//        storageManager.loadStorageServices();

//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/psc/view/baseApp.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/psc/gui/register.fxml"));
//        loader.setController(new RegisterController(new RegisterFlowControl(), screens));


        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("/ch/psc/gui/styles.css");

        primaryStage.setOnCloseRequest(event -> exit(primaryStage, user));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pretty Secure Cloud");
        primaryStage.getIcons().add(new Image("images/logo/logo.png"));
        primaryStage.show();
    }

    public void exit(Stage stage, Optional<User> user) {
        user.ifPresent(User::save);
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    private Optional<User> loadUser() {
        try {

            return Optional.ofNullable(new JSONWriterReader().readFromJson("user.json", User.class));
        } catch (Exception e) {
            //    Map<StorageService, Map<String, String>> storageServiceConfig = new HashMap<>();
         /*   storageServiceConfig.put(StorageService.DROPBOX, Collections.singletonMap("access_token", "password1"));
            storageServiceConfig.put(StorageService.LOCAL, Collections.singletonMap("path", "/path/to/folder"));
            storageServiceConfig.put(StorageService.GOOGLE_DRIVE, Collections.singletonMap("access_token", "password1"));*/
            return Optional.empty();// new User("name", "email", "pwd", storageServiceConfig);
        }
    }


}
