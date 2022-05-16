package ch.psc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.common.context.AuthenticationContext;
import ch.psc.domain.user.JSONAuthenticationService;
import ch.psc.gui.Config;
import ch.psc.gui.ControlledScreen;
import ch.psc.gui.FileBrowserController;
import ch.psc.gui.LoginController;
import ch.psc.gui.SignUpController;
import ch.psc.gui.util.JavaFxUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * @author SandroGuerotto
 */
public class PrettySecureCloud extends Application {

  private final Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens = new HashMap<>();
  private Stage primaryStage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    AuthenticationContext.setAuthService(new JSONAuthenticationService(new JSONWriterReader()));

    this.primaryStage = primaryStage;
    loadAllControlledScreens();
    setStartScreen(JavaFxUtils.RegisteredScreen.LOGIN_PAGE);
    primaryStage.setMinHeight(Config.MIN_HEIGHT);
    primaryStage.setMinWidth(Config.MIN_WIDTH);
    primaryStage.setOnCloseRequest(event -> exit(primaryStage));
    primaryStage.show();
  }

  /**
   * Sets up scene and sets start screen. Sets stylesheet and stage icon.
   *
   * @param startScreen first screen to show
   */
  private void setStartScreen(JavaFxUtils.RegisteredScreen startScreen) {
    ControlledScreen start = screens.get(startScreen);
    Scene scene = new Scene(start.getRoot());
    primaryStage.setScene(scene);
    primaryStage.setTitle(startScreen.getTitel());
    primaryStage.getIcons().add(new Image("images/logo/logo.png"));
    scene.getStylesheets().add("ch/psc/gui/styles.css");
  }

  /**
   * Saves user data and exits application.
   *
   * @param stage primary stage
   */
  public void exit(Stage stage) {
    stage.close();
    Platform.exit();
    System.exit(0);
  }

  /**
   * Loads all screens which have been "registered" in the enum {@code RegisteredScreen}
   */
  private void loadAllControlledScreens() {
    loadScreen(JavaFxUtils.RegisteredScreen.LOGIN_PAGE,
        new LoginController(primaryStage, screens, AuthenticationContext.getAuthService()));
    loadScreen(JavaFxUtils.RegisteredScreen.SIGNUP_PAGE,
        new SignUpController(primaryStage, screens, AuthenticationContext.getAuthService()));
    loadScreen(JavaFxUtils.RegisteredScreen.FILE_BROWSER_PAGE,
        new FileBrowserController(primaryStage, screens));
    // add other screens here
  }

  /**
   * Loads the screen given as a parameter. Sets controller to its FXML.
   *
   * @param screen screen info (FXML File Name and Fixed Title)
   */
  private void loadScreen(JavaFxUtils.RegisteredScreen screen, ControlledScreen screenController) {
    try {
      FXMLLoader loader =
          new FXMLLoader(ControlledScreen.class.getResource(screen.getFxmlFileName()),
              ResourceBundle.getBundle(Config.TEXT_BUNDLE_NAME));

      loader.setController(screenController);
      loader.load();
      screens.put(screen, screenController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
