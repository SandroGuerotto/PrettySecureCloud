package ch.psc.presentation.controller;

import ch.psc.presentation.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Controller for the LoginView.
 * Contains everything the controller has to reach in the login view and all methods the login view calls based on events.
 *
 * @author
 * @version 1.0
 */

public class LoginViewController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField enterMailTextfield;

    @FXML
    private PasswordField enterPasswordTextfield;

    @FXML
    private AnchorPane pane;

    private final Map<Screens, Pane> screens;

    public LoginViewController(Map<Screens, Pane> screens){
        this.screens = screens;
    }


    @FXML
    public void initialize(){
    }

    /**
     * Register screen will be shown.
     */
    @FXML
    private void register(){
        enterMailTextfield.clear();
        enterPasswordTextfield.clear();
        registerButton.getScene().setRoot(screens.get(Screens.REGISTER));
    }

    /**
     * Logindata of user will be checked and if approved the filebrowser will be shown.
     */
    @FXML
    private void login(){
        loginButton.setOnAction(event -> {
            enterMailTextfield.getText();
            enterPasswordTextfield.getText();
            //Todo: Validation of login
        });


    }

}
