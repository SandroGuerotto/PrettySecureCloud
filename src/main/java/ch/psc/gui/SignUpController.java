package ch.psc.gui;

import ch.psc.domain.storage.service.StorageService;
import ch.psc.domain.user.User;
import ch.psc.gui.components.signUp.SignUpFlowControl;
import ch.psc.gui.util.JavaFxUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


public class SignUpController extends ControlledScreen {


    private final SignUpFlowControl flowControl;

    @FXML
    private VBox registerMainPane;

    public SignUpController(SignUpFlowControl flowControl, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens,
                            Stage primaryStage) {
        super(primaryStage, screens);
        this.flowControl = flowControl;
    }

    @FXML
    private void initialize() {
        flowControl.getCurrentPosition().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && !flowControl.isDone()) {
                buildControl();
            }
        });
        flowControl.isDoneProperty().addListener((observable, old, newValue) -> finish());
        flowControl.next();
    }

    private void finish() {
        List<Object> data = flowControl.getData();
//        User user =
        new User(
                (String) data.get(0), (String) data.get(1), (String) data.get(2),
                (Map<StorageService, Map<String, String>>) data.get(3)
        ).save();
//        new StorageManager(user);
        try { //todo replace with real switch to browser
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/psc/view/baseApp.fxml"));
            registerMainPane.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        registerMainPane.getScene().setRoot(screens.get(Screens.FILE_BROWSER)); //TODO
    }

    private void buildControl() {
        registerMainPane.getChildren().clear();


        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(15, 0, 0, 0));
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        Button prev = new Button("Prev");
        prev.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT));
        prev.getStyleClass().add("control-button");

        Button next = new Button("Next");
        next.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_RIGHT));
        next.setContentDisplay(ContentDisplay.RIGHT);
        next.getStyleClass().add("control-button");

        prev.setOnAction(event -> flowControl.previous());
        prev.setCancelButton(true);
        next.setOnAction(event -> {
            if (flowControl.isValid()) flowControl.next();
        });

        pane.getChildren().add(prev);

        IntStream labels = IntStream.range(0, flowControl.getSteps());
        labels.forEach(value -> {
            pane.getChildren().add(new FontAwesomeIconView(value == flowControl.getCurrentPosition().get() ? FontAwesomeIcon.CIRCLE : FontAwesomeIcon.CIRCLE_THIN));
        });

        pane.getChildren().add(next);

        registerMainPane.getChildren().addAll(
                flowControl.getCurrentPane(),
                pane
        );
    }

    @Override
    protected Parent getRoot() {
        return registerMainPane;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.REGISTER_PAGE;
    }
}
