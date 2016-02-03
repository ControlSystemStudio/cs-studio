package org.csstudio.saverestore.ui.browser;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.saverestore.ui.SnapshotViewerEditor;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.UnfocusableButton;
import org.eclipse.ui.IWorkbenchSite;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

/**
 *
 * <code>DefaultBaseLevelBrowser</code> is an implementation of the base level browser, which uses a list to select the
 * base level from. It also provides an input text, where a non existing base level can be selected.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultBaseLevelBrowser extends GridPane implements BaseLevelBrowser<BaseLevel> {

    private static final String ANIMATED_STYLE = SnapshotViewerEditor.ANIMATED_STYLE + "-fx-font-weight: bold; ";

    private final ListView<BaseLevel> baseLevelsList;
    private final Button okButton;
    private final TextField textField;
    private final FadeTransition animation;

    private ObjectProperty<BaseLevel> selectedBaseLevelProperty;
    private ObjectProperty<BaseLevel> internalBaseLevelProperty;
    private ObjectProperty<List<BaseLevel>> availableBaseLevelsProperty;

    private final IWorkbenchSite parent;

    /**
     * Constructs a new default base level browser for the given view.
     *
     * @param view the parent view (needed only to obtain the shell)
     */
    public DefaultBaseLevelBrowser(IWorkbenchSite view) {
        this.parent = view;
        setVgap(5);
        setHgap(5);
        baseLevelsList = new ListView<>();
        baseLevelsList.selectionModelProperty().get().setSelectionMode(SelectionMode.SINGLE);
        baseLevelsList.selectionModelProperty().get().selectedItemProperty()
            .addListener((a, o, n) -> baseSelected(true));
        baseLevelsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                confirmSelectBaseLevel();
            }
        });
        baseLevelsList.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        okButton = new UnfocusableButton("Select");
        okButton.setOnAction(e -> confirmSelectBaseLevel());
        textField = new TextField();
        textField.textProperty().addListener((a, o, n) -> baseSelected(false));
        textField.setMaxWidth(Double.MAX_VALUE);
        setGridConstraints(textField, true, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(okButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        setGridConstraints(textField, true, true, Priority.ALWAYS, Priority.ALWAYS);
        add(baseLevelsList, 0, 0, 2, 1);
        add(textField, 0, 1, 1, 1);
        add(okButton, 1, 1, 1, 1);
        animation = new FadeTransition(Duration.seconds(0.15), okButton);
        animation.setAutoReverse(true);
        animation.setFromValue(1.0);
        animation.setToValue(0.4);
        animation.setCycleCount(6);
        setMinHeight(200);
    }

    private void baseSelected(boolean fromList) {
        BaseLevel n = baseLevelsList.selectionModelProperty().get().getSelectedItem();
        if (fromList) {
            if (n == null) {
                textField.setText("");
            } else {
                textField.setText(n.getStorageName());
            }
        } else {
            String s = textField.getText();
            n = new BaseLevel(n == null ? null : n.getBranch(), s, s);
        }
        internalBaseLevelProperty().setValue(n);
    }

    private void confirmSelectBaseLevel() {
        BaseLevel bl = internalBaseLevelProperty().getValue();
        if (bl != null && !availableBaseLevelsProperty.get().contains(bl)) {
            String message = Selector.validateBaseLevelName(bl.getStorageName());
            if (message != null && !FXMessageDialog.openQuestion(parent.getShell(), "Invalid Name",
                message + "\n Do you still want to continue?")) {
                return;
            }
        }
        selectedBaseLevelProperty().setValue(bl);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#getFXContent()
     */
    @Override
    public Node getFXContent() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#setShowOnlyAvailable(boolean)
     */
    @Override
    public void setShowOnlyAvailable(boolean onlyAvailable) {
        textField.setDisable(onlyAvailable);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#availableBaseLevelsProperty()
     */
    @Override
    public ObjectProperty<List<BaseLevel>> availableBaseLevelsProperty() {
        if (availableBaseLevelsProperty == null) {
            availableBaseLevelsProperty = new SimpleObjectProperty<>(new ArrayList<>());
            availableBaseLevelsProperty
                .addListener((a, o, n) -> baseLevelsList.getItems().setAll(availableBaseLevelsProperty.get()));
        }
        return availableBaseLevelsProperty;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#transform(java.util.List)
     */
    @Override
    public List<BaseLevel> transform(List<? extends BaseLevel> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#selectedBaseLevelProperty()
     */
    @Override
    public Property<BaseLevel> selectedBaseLevelProperty() {
        if (selectedBaseLevelProperty == null) {
            selectedBaseLevelProperty = new SimpleObjectProperty<>(this, "selectedBaseLevel", null);
            selectedBaseLevelProperty.addListener((a, o, n) -> {
                baseLevelsList.selectionModelProperty().get().select(n);
                animation.pause();
                animation.jumpTo(Duration.seconds(0));
                animation.stop();
                okButton.setStyle(null);
            });
        }
        return selectedBaseLevelProperty;
    }

    private Property<BaseLevel> internalBaseLevelProperty() {
        if (internalBaseLevelProperty == null) {
            internalBaseLevelProperty = new SimpleObjectProperty<>(this, "internalSelectedBaseLevel", null);
            internalBaseLevelProperty.addListener((a, o, n) -> {
                if (n != null) {
                    BaseLevel iso = selectedBaseLevelProperty().getValue();
                    if (!n.equals(iso) && animation.getStatus() != Status.RUNNING) {
                        okButton.setStyle(ANIMATED_STYLE);
                        animation.play();
                    } else if (n.equals(iso)) {
                        animation.pause();
                        animation.jumpTo(Duration.seconds(0));
                        animation.stop();
                        okButton.setStyle(null);
                    }
                }
            });
        }
        return internalBaseLevelProperty;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#getTitleFor(java.util.Optional, java.util.Optional)
     */
    @Override
    public String getTitleFor(Optional<BaseLevel> baseLevel, Optional<String> branch) {
        if (baseLevel.isPresent()) {
            if (branch.isPresent()) {
                return baseLevel.get().getPresentationName() + " (" + branch.get() + ")";
            } else {
                return baseLevel.get().getPresentationName();
            }
        } else {
            return "";
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#getReadableName()
     */
    @Override
    public String getReadableName() {
        return "Default Browser";
    }
}
