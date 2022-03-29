package ch.psc.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class BaseAppController {
  
  @FXML
  private MenuBar menuBar;
  
  @FXML
  private AnchorPane anchorContent;
  
  @FXML
  private void initialize() {
    ImageView logo = new ImageView("images/logo/logo_text_down.png");
    anchorContent.getChildren().add(logo);
  }

}
