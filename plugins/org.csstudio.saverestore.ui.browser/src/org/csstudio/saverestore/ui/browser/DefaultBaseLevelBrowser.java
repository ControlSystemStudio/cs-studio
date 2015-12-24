package org.csstudio.saverestore.ui.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.ui.fx.util.FXMessageDialog;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

public class DefaultBaseLevelBrowser extends GridPane implements BaseLevelBrowser<BaseLevel> {

    private static final String ANIMATED_STYLE = "-fx-background-color: #FF8080; -fx-text-fill: white; "
        + "-fx-font-weight: bold; " + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1);";

    private ListView<BaseLevel> baseLevelsList;
    private Button okButton;
    private TextField textField;
    private FadeTransition animation;

    private ObjectProperty<BaseLevel> selectedBaseLevelProperty;
    private ObjectProperty<BaseLevel> internalBaseLevelProperty;
    private ObjectProperty<List<BaseLevel>> availableBaseLevelsProperty;
    private boolean showOnlyAvailable = false;

    private final BrowserView parent;

    public DefaultBaseLevelBrowser(BrowserView view) {
        this.parent = view;
        setVgap(5);
        setHgap(5);
        baseLevelsList = new ListView<>();
        baseLevelsList.selectionModelProperty().get().setSelectionMode(SelectionMode.SINGLE);
        baseLevelsList.selectionModelProperty().get().selectedItemProperty()
            .addListener((a, o, n) -> baseSelected(true));
        baseLevelsList.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        okButton = new Button("Select");
        okButton.setOnAction(e -> {
            BaseLevel bl = internalBaseLevelProperty().getValue();
            if (bl != null) {
                String message = Selector.validateBaseLevelName(bl.getStorageName());
                if (message != null) {
                    FXMessageDialog.openWarning(parent.getSite().getShell(), "Invalid Name", message);
                    return;
                }
            }
            selectedBaseLevelProperty().setValue(bl);
        });
        textField = new TextField();
        textField.textProperty().addListener((a, o, n) -> baseSelected(false));
        textField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillHeight(textField, false);
        GridPane.setFillWidth(textField, true);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setVgrow(textField, Priority.NEVER);
        GridPane.setFillHeight(okButton, false);
        GridPane.setFillWidth(okButton, false);
        GridPane.setHgrow(okButton, Priority.NEVER);
        GridPane.setVgrow(okButton, Priority.NEVER);
        GridPane.setHalignment(okButton, HPos.RIGHT);
        GridPane.setFillHeight(baseLevelsList, true);
        GridPane.setFillWidth(baseLevelsList, true);
        GridPane.setHgrow(baseLevelsList, Priority.ALWAYS);
        GridPane.setVgrow(baseLevelsList, Priority.ALWAYS);
        add(baseLevelsList, 0, 0, 2, 1);
        add(textField, 0, 1, 1, 1);
        add(okButton, 1, 1, 1, 1);
        animation = new FadeTransition(Duration.seconds(0.15), okButton);
        animation.setAutoReverse(true);
        animation.setFromValue(1.0);
        animation.setToValue(0.4);
        animation.setCycleCount(6);
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
        if (this.showOnlyAvailable == onlyAvailable)
            return;
        this.showOnlyAvailable = onlyAvailable;
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
            availableBaseLevelsProperty.addListener((a, o, n) -> {
                baseLevelsList.getItems().setAll(availableBaseLevelsProperty.get());
            });
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
        List<BaseLevel> transformation = new ArrayList<>();
        list.forEach(e -> transformation.add(e));
        return transformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#selectedBaseLevelProperty()
     */
    @Override
    public Property<BaseLevel> selectedBaseLevelProperty() {
        if (selectedBaseLevelProperty == null) {
            selectedBaseLevelProperty = new SimpleObjectProperty<BaseLevel>(this, "selectedBaseLevel", null);
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
            internalBaseLevelProperty = new SimpleObjectProperty<BaseLevel>(this, "internalSelectedBaseLevel", null);
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
