package ch.psc;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.user.User;
import ch.psc.gui.ControlledScreen;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.presentation.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


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
        Optional<User> user = loadUser(); // TODO user handling



        this.primaryStage = primaryStage;
        loadAllControlledScreens();
        setStartScreen(JavaFxUtils.RegisteredScreen.LOGIN_PAGE);
        //setStartScreen(JavaFxUtils.RegisteredScreen.SIGNUP_PAGE);
        primaryStage.setMinHeight(Config.MIN_HEIGHT);
        primaryStage.setMinWidth(Config.MIN_WIDTH);
        primaryStage.setOnCloseRequest(event -> exit(primaryStage, user));
        primaryStage.show();
    }

    /**
     * Sets up scene and sets start screen.
     * Sets stylesheet and stage icon.
     *
     * @param startScreen first screen to show
     */
    private void setStartScreen(JavaFxUtils.RegisteredScreen startScreen) {
        ControlledScreen start = screens.get(startScreen);
        Scene scene = new Scene(start.getRoot());
        primaryStage.setScene(scene);
        primaryStage.setTitle(startScreen.getTitel());
        primaryStage.getIcons().add(new Image("images/logo/logo.png"));
        scene.getStylesheets().add("/ch/psc/gui/styles.css");
    }

    /**
     * Saves user data and exits application.
     *
     * @param stage primary stage
     * @param user  logged in user
     */
    public void exit(Stage stage, Optional<User> user) {
        user.ifPresent(User::save);
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Loads all screens which have been "registered" in the enum {@code RegisteredScreen}
     */
    private void loadAllControlledScreens() {
        Arrays.stream(JavaFxUtils.RegisteredScreen.values())
                .forEach(this::loadScreen);
    }

    /**
     * Loads user data from JSON.
     *
     * @return user data
     */
    private Optional<User> loadUser() {
        try {

            return Optional.ofNullable(new JSONWriterReader().readFromJson("user.json", User.class));
        } catch (Exception e) {
            //    Map<StorageService, Map<String, String>> storageServiceConfig = new HashMap<>();
            return Optional.empty();// new User("name", "email", "pwd", storageServiceConfig);
        }
    }

    /**
     * Loads the screen given as a parameter.
     * Sets controller to its FXML.
     *
     * @param screen screen info (FXML File Name and Fixed Title)
     */
    private void loadScreen(JavaFxUtils.RegisteredScreen screen) {
        try {
            FXMLLoader loader = new FXMLLoader(ControlledScreen.class.getResource(screen.getFxmlFileName()),
                    ResourceBundle.getBundle(Config.TEXT_BUNDLE_NAME));

            ControlledScreen screenController = screen.getControllerClass()
                    .getDeclaredConstructor(Stage.class, Map.class)
                    .newInstance(primaryStage, screens);

            loader.setController(screenController);
            loader.load();
            screens.put(screen, screenController);
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
