package ch.psc.presentation;


import ch.psc.gui.ControlledScreen;
import ch.psc.gui.util.JavaFxUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PrettySecureCloud extends Application {
  private final Map<JavaFxUtils.RegistrierterScreen, ControlledScreen> screens = new HashMap<>();
  private Stage primaryStage;
  
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage){
    this.primaryStage = primaryStage;
    loadAllControlledScreens();
    primaryStage.setMinHeight(Config.MIN_HEIGHT);
    primaryStage.setMinWidth(Config.MIN_WIDTH);
    primaryStage.show();

  }

  /**
   * Loads all screens which have been "registered" in the enum {@code RegisteredScreen}
   */
  private void loadAllControlledScreens() {
    for (JavaFxUtils.RegistrierterScreen screen : JavaFxUtils.RegistrierterScreen.values()) {
      loadScreen(screen);
    }
  }

  /**
   * Loads the screen given as a parameter
   *
   * @param screen screen info (FXML File Name and Fixed Title)
   */
  private void loadScreen(JavaFxUtils.RegistrierterScreen screen) {
    try {
      FXMLLoader loader = new FXMLLoader(ControlledScreen.class.getResource(screen.getFxmlFileName()),
              ResourceBundle.getBundle(Config.TEXT_BUNDLE_NAME));
      Parent parent = loader.load();
      ControlledScreen screenController = loader.getController();
      screenController.setScreens(screens);
      screenController.setPrimaryStage(primaryStage);
      if (screen == JavaFxUtils.RegistrierterScreen.LOGINPAGE) {
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle(screen.getTitel());
      }
      screens.put(screen, screenController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
