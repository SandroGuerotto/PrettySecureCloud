package ch.psc.gui.components.signUp;

import ch.psc.domain.cipher.CipherAlgorithms;
import ch.psc.domain.cipher.CipherFactory;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.cipher.PscCipher;
import ch.psc.exceptions.FatalImplementationException;
import com.jfoenix.controls.JFXComboBox;
import ch.psc.gui.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles all user data to customize his encryption.
 * The user can choose what level of encryption he wants to use for encrypting his data.
 *
 * @author SandroGuerotto
 */
public class ChooseEncryption extends VBox implements SignUpFlow {

    private final Map<String, Key> generatedKeys = new HashMap<>();

    /**
     * Creates encryption algorithm picker.
     */
    public ChooseEncryption() {
        initialize();
    }

    /**
     * Initializes form and creates a list of supported {@link PscCipher}.
     */
    private void initialize() {
        this.setMinHeight(250);
        this.setSpacing(50);
        this.setPadding(new Insets(10, 20, 10, 20));

        JFXComboBox<CipherAlgorithms> algorithms = createStorageDropDown(Arrays.stream(CipherAlgorithms.values())
                .filter(CipherAlgorithms::isSupported)
                .collect(Collectors.toList()));


        Label title = new Label(Config.getResourceText("signup.title.chooseEncryption"));
        FlowPane servicePane = new FlowPane();

        servicePane.getChildren().addAll(algorithms);

        this.getChildren().addAll(title, servicePane);
    }

    /**
     * Creates a dropdown for a {@link CipherAlgorithms}.
     *
     * @param cipherAlgorithms supported algorithms for ciphers
     * @return designed button
     */
    private JFXComboBox<CipherAlgorithms> createStorageDropDown(List<CipherAlgorithms> cipherAlgorithms) {
        ObservableList<CipherAlgorithms> algorithms = FXCollections.observableArrayList();
        algorithms.addAll(cipherAlgorithms);

        JFXComboBox<CipherAlgorithms> cipherAlgorithmDropDown = new JFXComboBox<>();
        cipherAlgorithmDropDown.setItems(algorithms);

        cipherAlgorithmDropDown.setOnAction(e -> register(cipherAlgorithmDropDown.getValue()));
        return cipherAlgorithmDropDown;
    }

    /**
     * Invoked by a cipher algorithm dropdown selection.
     * The relevant cipher algorithm value is claimed from the CipherFactory
     * Delegates registration setup to the corresponding method.
     *
     * @param cipherAlgorithms uses selected service
     */
    private void register(CipherAlgorithms cipherAlgorithms) {

        try {
            PscCipher pscCipher = CipherFactory.createCipher(cipherAlgorithms.name());
            generatedKeys.putAll(pscCipher.generateKey());

        } catch (FatalImplementationException e) {
            e.printStackTrace(); //TODO sophisticated error logging
        }
    }

    @Override
    public List<Object> getData() {
        return List.of(generatedKeys);
    }

    @Override
    public boolean isValid() {
//        return !generatedKeys.isEmpty();
        return true;//TODO remove after testing
    }

    @Override
    public void clear() {
        generatedKeys.clear();
    }
}
