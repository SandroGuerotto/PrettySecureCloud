package ch.psc.gui.components.signUp;

import ch.psc.domain.cipher.Key;
import ch.psc.presentation.Config;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * Handles all user data to customize his encryption.
 * The user can choose what level of encryption he wants to use for encrypting his data.
 *
 * @author SandroGuerotto
 */
public class ChooseEncryption extends VBox implements SignUpFlow {
    public ChooseEncryption() {
        //TODO build cipher keys

        Label title = new Label(Config.getResourceText("signup.encryption.title"));
        this.getChildren().add(title);
        this.setMinHeight(250);
        this.setSpacing(50);
        this.setPadding(new Insets(10, 20, 10, 20));
    }

    @Override
    public List<Object> getData() {
        Map<String, Key> encryption = new HashMap<>();
        encryption.put("Test",new Key(new SecretKeySpec("test".getBytes(), "MD5")));
        return Collections.singletonList(encryption);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void clear() {

    }
}
