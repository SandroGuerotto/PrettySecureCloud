package ch.psc.presentation;

import ch.psc.presentation.controller.LoginViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PrettySecureCloud extends Application {
    Map<Screens, Pane> screens;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        screens = loadViews();

        //Scene scene = new Scene(baseApp);
        //TODO add stylesheet

        primaryStage.setScene(new Scene(screens.get(Screens.LOGIN)));
        primaryStage.setTitle("Pretty Secure Cloud");
        primaryStage.getIcons().add(new Image("images/logo/logo.png"));
        primaryStage.show();
    }

    private Map<Screens, Pane> loadViews() {

        Map<Screens, Pane> screens = new HashMap<>();
        try {
          //FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/psc/view/baseApp.fxml"));
          //VBox baseApp = loader.load();
            FXMLLoader loader= new FXMLLoader(getClass().getResource("/ch/psc/view/LoginView.fxml"));
            loader.setController(new LoginViewController(screens));
            screens.put(Screens.LOGIN, loader.load());
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return screens;
    }
}
