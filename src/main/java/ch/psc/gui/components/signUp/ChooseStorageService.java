package ch.psc.gui.components.signUp;

import ch.psc.domain.storage.service.DropBoxService;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.gui.Config;
import com.dropbox.core.DbxWebAuth;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all sign-in process of the supported storage services.
 * Each Service has an individual implementation of their authorization process.
 *
 * @author SandroGuerotto
 */
public class ChooseStorageService extends VBox implements SignUpFlow {

    private final Map<StorageService, Map<String, String>> chosenServices = new HashMap<>();

    /**
     * Creates storage service picker.
     */
    public ChooseStorageService() {
        initialize();
    }

    /**
     * Initializes form and creates a list of supported {@link StorageService}.
     */
    private void initialize() {
        this.setMinHeight(250);
        this.setSpacing(50);
        this.setPadding(new Insets(10, 20, 10, 20));

        List<JFXButton> services = Arrays.stream(StorageService.values())
                .filter(StorageService::isSupported)
                .map(this::createStorageButton)
                .collect(Collectors.toList());


        Label title = new Label(Config.getResourceText("signup.title.chooseService"));
        FlowPane servicePane = new FlowPane();

        servicePane.getChildren().addAll(services);

        this.getChildren().addAll(title, servicePane);
    }

    /**
     * Creates a button for a {@link StorageService}.
     * Adds a graphic to the button.
     *
     * @param storageService supported service
     * @return designed button
     */
    private JFXButton createStorageButton(StorageService storageService) {
        JFXButton button = new JFXButton();

//        button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DROPBOX,"75"));
        ImageView icon = new ImageView(storageService.getImagePath());
        icon.setPreserveRatio(true);
        icon.setFitWidth(70);
        button.setGraphic(icon);
        button.getStyleClass().add("jfx-button-" + storageService.name());

        button.setPadding(new Insets(10));
        button.setContentDisplay(ContentDisplay.CENTER);
        button.setOnAction(e -> register(storageService));
        return button;
    }

    /**
     * Invoked by a storage service button.
     * Delegates registration setup to the corresponding method.
     *
     * @param storageService uses selected service
     */
    private void register(StorageService storageService) {
        switch (storageService) {
            case DROPBOX:
                registerDropBox();
            case GOOGLE_DRIVE:
            case LOCAL:
        }
    }

    /**
     * Initializes Dropbox auth requests and configuration to use them in the popup.
     */
    private void registerDropBox() {
        DropBoxService dropBoxService = new DropBoxService();
        DbxWebAuth.Request authRequest = dropBoxService.buildAuthRequest();
        DbxWebAuth auth = dropBoxService.createDbxWebAuth();
        Stage dialog = createDropboxPopup(dropBoxService, auth, authRequest);
        dialog.show();
    }

    /**
     * Creates a popup with instructions to connect with a dropbox account.
     * The user is required to log in and grants permission to access his data.
     * After, a token needs to be copied into an input field.
     * If the token is valid, it gets automatically converted into an access-token and inserted into {@link #chosenServices}.
     * The popup closes.
     */
    private Stage createDropboxPopup(final DropBoxService dropBoxService, final DbxWebAuth auth, final DbxWebAuth.Request authRequest) {
        final TextField codeInput = new TextField();
        final Stage dialog = new Stage();
        String authorizeUrl = auth.authorize(authRequest);

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(null);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(10));
        codeInput.setPromptText(Config.getResourceText("signup.dropbox.prompt"));

        codeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                try {
                    Map<String, String> accessToken = dropBoxService.finishFromCode(auth, newValue);
                    chosenServices.put(StorageService.DROPBOX, accessToken);
                    dialog.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Hyperlink hyperlink = new Hyperlink(String.format(Config.getResourceText("signup.dropbox.link"), authorizeUrl));
        hyperlink.setOnAction(event -> JavaFxUtils.openInBrowser(authorizeUrl));

        dialogVbox.getChildren().addAll(hyperlink,
                new Text(Config.getResourceText("signup.dropbox.step2")),
                new Text(Config.getResourceText("signup.dropbox.step3")),
                codeInput);
        Scene dialogScene = new Scene(dialogVbox, 500, 300);
        dialog.setScene(dialogScene);
        return dialog;
    }

    @Override
    public List<Object> getData() {
        return Collections.singletonList(chosenServices);
    }

    @Override
    public boolean isValid() {
//        return !chosenServices.isEmpty();
        return true; //TODO remove after testing
    }

    @Override
    public void clear() {

    }
}
