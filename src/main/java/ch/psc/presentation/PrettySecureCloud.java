package ch.psc.presentation;


import ch.psc.gui.ControlledScreen;
import ch.psc.gui.util.JavaFxUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PrettySecureCloud extends Application {
    private final Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens = new HashMap<>();
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadAllControlledScreens();
        setStartScreen(JavaFxUtils.RegisteredScreen.LOGIN_PAGE);
        primaryStage.setMinHeight(Config.MIN_HEIGHT);
        primaryStage.setMinWidth(Config.MIN_WIDTH);
        primaryStage.show();

    }

    private void setStartScreen(JavaFxUtils.RegisteredScreen startScreen) {
        ControlledScreen start = screens.get(JavaFxUtils.RegisteredScreen.LOGIN_PAGE);
//        Scene scene = new Scene(start.getRoot());
//        primaryStage.setScene(scene);
//        primaryStage.setTitle(startScreen.getTitel());
//        scene.getStylesheets().add("/ch/psc/gui/styles.css");
    }

    /**
     * Loads all screens which have been "registered" in the enum {@code RegisteredScreen}
     */
    private void loadAllControlledScreens() {
        Arrays.stream(JavaFxUtils.RegisteredScreen.values())
                .forEach(this::loadScreen);
    }

    /**
     * Loads the screen given as a parameter
     *
     * @param screen screen info (FXML File Name and Fixed Title)
     */
    private void loadScreen(JavaFxUtils.RegisteredScreen screen) {
        try {
            FXMLLoader loader = new FXMLLoader(ControlledScreen.class.getResource(screen.getFxmlFileName()),
                    ResourceBundle.getBundle(Config.TEXT_BUNDLE_NAME));
            ControlledScreen screenController = (ControlledScreen) screen.getControllerClass()
                    .getDeclaredConstructor().newInstance(primaryStage, screens);
            loader.setController(screenController);
            Parent parent = loader.load();
//            ControlledScreen screenController = loader.getController();
//            screenController.setScreens(screens);
//            screenController.setPrimaryStage(primaryStage);
            screens.put(screen, screenController);
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
