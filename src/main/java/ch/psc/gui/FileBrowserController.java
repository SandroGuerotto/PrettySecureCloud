package ch.psc.gui;

import ch.psc.domain.common.context.UserContext;
import ch.psc.domain.storage.StorageManager;
import ch.psc.gui.util.JavaFxUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Dummy controller
 * @author Sandro
 */
public class FileBrowserController extends  ControlledScreen{
    private StorageManager storageManager;
    public FileBrowserController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {
        super(primaryStage, screens);
    }

    @Override
    protected boolean init(JavaFxUtils.RegisteredScreen previousScreen, Object... params) {
        storageManager = new StorageManager(UserContext.getAuthorizedUser());
        return super.init(previousScreen, params);
    }

    @Override
    public Parent getRoot() {
        return null;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return null;
    }

    // TODO on logout or close: destroy storageManager and set User = null
}
