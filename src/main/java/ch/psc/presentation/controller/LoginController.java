package ch.psc.presentation.controller;

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
 * @author waldbsaf
 * @version 1.0
 */

public class LoginController {

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
        //registerButton.getScene().setRoot(screens.get(Screens.REGISTER));
    }

    /**
     * Logindata of user will be checked and if approved the filebrowser will be shown.
     */
    @FXML
    private void login(){
            enterMailTextfield.getText();
            enterPasswordTextfield.getText();
            //Todo: Validation of login


    }
   /* @Override
    public Parent getRoot() {
        return loginPane;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.LOGIN_PAGE;
    }*/

}
