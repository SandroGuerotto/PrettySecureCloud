package ch.psc.presentation.controller.register;

import ch.psc.domain.storage.service.DropBoxService;
import ch.psc.domain.storage.service.StorageService;
import com.dropbox.core.DbxWebAuth;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.*;

public class ChooseStorageService extends VBox implements RegisterFlow {

    private final Map<StorageService, Map<String, String>> choosenServices = new HashMap<>();

    public ChooseStorageService() {
        this.setMinHeight(250);
        this.setSpacing(50);
        this.setPadding(new Insets(10, 20, 10, 20));

        List<Button> services = Arrays.stream(StorageService.values())
                .filter(StorageService::isSupported)
                .map(this::createStorageButton)
                .toList();

        Label title = new Label("Choose your cloud storage");
        FlowPane servicePane = new FlowPane();

        servicePane.getChildren().addAll(services);

        this.getChildren().addAll(title, servicePane);
    }

    private Button createStorageButton(StorageService storageService) {
        Button button = new Button();

        button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DROPBOX,"75"));
        button.getStyleClass().add("button-"+storageService.name());

        button.setPadding(new Insets(10));
        button.setContentDisplay(ContentDisplay.CENTER);
        button.setOnAction(e -> register(storageService));
        return button;
    }


    private void register(StorageService storageService) {
        switch (storageService) {
            case DROPBOX:
                registerDropBox();
            case GOOGLE_DRIVE:
            case LOCAL:
        }
    }

    private void registerDropBox() {
        DropBoxService dropBoxService = new DropBoxService();
     /*   LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        String redirectUri = "";
        try {

            redirectUri = receiver.getRedirectUri();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        DbxWebAuth.Request authRequest = dropBoxService.buildAuthRequest();
        DbxWebAuth auth = dropBoxService.createDbxWebAuth();

        String authorizeUrl = auth.authorize(authRequest) + "&redirect_uri=localhost:8888";

        //TODO clean up
        final TextField codeInput = new TextField();
        final Stage dialog = new Stage();


//        receiver.waitForCode();

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(new Stage());
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(10, 10, 10, 20));
        codeInput.setPromptText("Copy the authorization code in here");

        codeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                try {
                    Map<String, String> accessToken = dropBoxService.finishFromCode(auth, newValue);
                    choosenServices.put(StorageService.DROPBOX, accessToken);
                    dialog.close();
                } catch (Exception e) {
                    e.printStackTrace(); //todo error handling
                }
            }
        });

        Hyperlink hyperlink = new Hyperlink("1. Go to " + authorizeUrl);
        hyperlink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(authorizeUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialogVbox.getChildren().addAll(
                hyperlink,
                new Text("2. Click \"Allow\" (you might have to log in first)."),
                new Text("3. Copy the authorization code."),
                codeInput
        );
        Scene dialogScene = new Scene(dialogVbox, 500, 300);
        dialog.setScene(dialogScene);
        dialog.show();
     /*   new Thread(() -> { //TODO maybe this can work
            try {
                System.out.println("waiting");
                String code = receiver.waitForCode();
                System.out.println(code);
//                Thread.sleep(5000);
            } catch (IOException e) {
                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }).start();*/


    }

    @Override
    public List<Object> getData() {
        return Collections.singletonList(choosenServices);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
