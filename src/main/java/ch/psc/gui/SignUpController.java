package ch.psc.gui;

import ch.psc.domain.cipher.Key;
import ch.psc.domain.common.context.UserContext;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.domain.user.AuthenticationService;
import ch.psc.domain.user.User;
import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.signUp.SignUpFlowControl;
import ch.psc.gui.util.JavaFxUtils;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * GUI Controller for sign-up process.
 *
 * @author SandroGuerotto, bananasprout
 */
public class SignUpController extends ControlledScreen {


    private SignUpFlowControl flowControl;
    private final AuthenticationService authenticationService;

    @FXML
    private HBox signupMainPane;

    @FXML
    private VBox signupFormPane;

    public SignUpController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens, AuthenticationService authenticationService) {
        super(primaryStage, screens);
        this.authenticationService = authenticationService;
        flowControl = new SignUpFlowControl();
    }

    @Override
    protected boolean init(JavaFxUtils.RegisteredScreen previousScreen, Object... params) {
        flowControl = new SignUpFlowControl();
        initialize();
        return super.init(previousScreen, params);
    }

    /**
     * Initializes components with listeners for flow control for registration purposes.
     */
    @FXML
    private void initialize() {
        flowControl.getCurrentPosition().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && !flowControl.isDone() && !flowControl.isCanceled()) {
                buildControl();
            }
        });
        flowControl.isDoneProperty().addListener((observable, old, newValue) -> {
            if (newValue) finish();
        });
        flowControl.isCanceledProperty().addListener((observable, old, newValue) -> {
            if (newValue) cancel();
        });
        flowControl.next();

        primaryStage.setMinHeight(Config.MIN_HEIGHT);
        primaryStage.setMinWidth(Config.MIN_WIDTH);
    }

    /**
     * Clear all data and switches back to login page.
     */
    private void cancel() {
        try {
            UserContext.setAuthorizedUser(null);
            switchScreen(JavaFxUtils.RegisteredScreen.LOGIN_PAGE);
        } catch (ScreenSwitchException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collects all data from sign-up process and creates a new user.
     * Switches screen to file browser.
     */
    private void finish() {
        try {
            UserContext.setAuthorizedUser(null);
            List<Object> data = flowControl.getData();
            User user = createUser(data);
            UserContext.setAuthorizedUser(authenticationService.signup(user));
            switchScreen(JavaFxUtils.RegisteredScreen.FILE_BROWSER_PAGE);
        } catch (AuthenticationException e) {
            e.printStackTrace(); // todo show error
        } catch (ScreenSwitchException e) {
            e.printStackTrace();
        }
//        example on how to use service
    }

    @SuppressWarnings("unchecked")
    private User createUser(List<Object> data) {
        Map<StorageService, Map<String, String>> services = (Map<StorageService, Map<String, String>>) data.get(4);
        Map<String, Key> keyChain = (Map<String, Key>) data.get(3);
        return new User((String) data.get(0), (String) data.get(1), (String) data.get(2), services, keyChain);
    }

    /**
     * Builds control panel at the bottom of each sign up step.
     */
    private void buildControl() {
        signupFormPane.getChildren().clear();


        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(15, 0, 0, 0));
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(20);
        JFXButton prev = new JFXButton(Config.getResourceText("signup.previous"));
        prev.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT));
        prev.getStyleClass().add("control-button");

        JFXButton next = new JFXButton(Config.getResourceText("signup.next"));
        next.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_RIGHT));
        next.setContentDisplay(ContentDisplay.RIGHT);
        next.getStyleClass().add("control-button");

        Label signUpErrorLabel = new Label();
        signUpErrorLabel.setPadding(new Insets(15, 0, 0, 0));
        signUpErrorLabel.getStyleClass().add("error");


        prev.setOnAction(event -> flowControl.previous());
        prev.setCancelButton(true);
        easterEgg(next);
        next.setOnAction(event -> {
            if (flowControl.isValid()) flowControl.next();
        });

        pane.getChildren().add(prev);

        IntStream labels = IntStream.range(0, flowControl.getSteps());
        labels.forEach(value ->
                pane.getChildren().add(new FontAwesomeIconView(
                        value == flowControl.getCurrentPosition().get() ? FontAwesomeIcon.CIRCLE : FontAwesomeIcon.CIRCLE_THIN))
        );

        pane.getChildren().add(next);

        signupFormPane.getChildren().addAll(
                flowControl.getCurrentPane(),
                signUpErrorLabel,
                pane
        );
    }

    private void easterEgg(JFXButton button) {
        button.setOnMouseClicked(event -> {
            if (!flowControl.isValid() && event.getClickCount() == 3) {
                Circle circle = new Circle(200);
                circle.setCenterX(button.getLayoutX() - 375);
                circle.setCenterY(button.getLayoutY() - 1);
                PathTransition transition = new PathTransition();
                transition.setNode(button);
                transition.setDuration(Duration.seconds(3));
                transition.setPath(circle);
                transition.setCycleCount(1);
                transition.play();
            }
        });
    }

    @Override
    public Parent getRoot() {
        return signupMainPane;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.SIGNUP_PAGE;
    }
}
