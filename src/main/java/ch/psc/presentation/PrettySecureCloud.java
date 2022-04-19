package ch.psc.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PrettySecureCloud extends Application {
  
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/psc/view/baseApp.fxml"));
    VBox baseApp = loader.load();

    Scene scene = new Scene(baseApp);
    //TODO add stylesheet
    
    primaryStage.setScene(scene);
    primaryStage.setTitle("Pretty Secure Cloud");
    primaryStage.getIcons().add(new Image("images/logo/logo.png"));
    primaryStage.show();
  }

}
