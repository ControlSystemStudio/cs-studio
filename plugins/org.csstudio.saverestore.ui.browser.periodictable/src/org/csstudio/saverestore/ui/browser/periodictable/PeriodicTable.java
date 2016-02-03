package org.csstudio.saverestore.ui.browser.periodictable;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.ui.browser.BaseLevelBrowser;
import org.csstudio.ui.fx.util.UnfocusableButton;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * <code>PeriodicTable</code> provides a periodic table, where user can select the element, isotope, and charge for
 * which the beamline sets will be loaded.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PeriodicTable extends GridPane implements BaseLevelBrowser<Isotope> {

    private class ElementButton extends UnfocusableButton {

        ElementButton(Element element) {
            init(element.symbol, 10);
            setStyle(element.getStyle());
            setTooltip(new Tooltip(element.fullName));
            setOnAction(e -> setSelectedElement(((Button) e.getSource()).getId()));
        }

        ElementButton(String text) {
            init(text, 8);
            setDisable(true);
        }

        private void init(String text, int fontSize) {
            setText(text);
            setId(text);
            setFont(Font.font(getFont().getFamily(), fontSize));
            setPadding(new Insets(1, 1, 1, 1));
            setPrefSize(BTN_SIZE, BTN_SIZE);
            setMaxSize(BTN_SIZE, BTN_SIZE);
            setMinSize(0, 0);
            setGridConstraints(this, false, false, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
            setCursor(Cursor.HAND);
        }
    }

    private static class ComboCell extends ListCell<Isotope> {
        @Override
        protected void updateItem(Isotope item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
            } else {
                setText(item.getPresentationName());
            }
        }
    }

    private static final int BTN_SIZE = 25;
    // style for the selected element button
    private static final String SELECTED_STYLE = "-fx-background-color: #8080FF; -fx-text-fill: white; "
        + "-fx-font-size: 9; -fx-font-weight: bold; "
        + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1);";

    // must not be final in order to be able to adjust the font size for different operating systems
    private String animatedStyle = "-fx-background-color: #FF8080; -fx-text-fill: white; "
        + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1); -fx-font-weight: bold; ";

    private ObjectProperty<Isotope> selectedBaseLevelProperty;
    private ObjectProperty<Isotope> internalIsotopeProperty;
    private ObjectProperty<List<Isotope>> availableIsotopesProperty;

    private List<Button> buttons;

    private Button isotopeButton;
    private final FadeTransition animation;
    private TinySpinner neutronSpinner;
    private TinySpinner chargeSpinner;
    private ComboBox<Isotope> isotopeCombo;
    private Label neutronLabel;
    private Label chargeLabel;
    private boolean showOnlyAvailable = false;

    /**
     * Constructs a new periodic table.
     */
    public PeriodicTable() {
        setVgap(0);
        setHgap(0);
        createButtons();
        createMainTable();
        add(createLanthActPane(), 3, 8, 15, 2);
        add(createIsotopePanel(), 2, 0, 10, 2);
        // 9 rows of elements + gap between table and lanthanides
        int height = 9 * BTN_SIZE + 5;
        setPrefHeight(height);
        setMinHeight(height);
        setMaxHeight(height);

        animation = new FadeTransition(Duration.seconds(0.15), isotopeButton);
        animation.setAutoReverse(true);
        animation.setFromValue(1.0);
        animation.setToValue(0.4);
        animation.setCycleCount(6);
        setShowOnlyAvailable(true);
    }

    private void createButtons() {
        Element[] elements = Element.values();
        buttons = new ArrayList<>(elements.length);
        for (int i = 0; i < elements.length; i++) {
            buttons.add(new ElementButton(elements[i]));
        }
    }

    private void createMainTable() {
        int index = 0;
        // add H and HE
        add(buttons.get(index++), 0, 0);
        add(buttons.get(index++), 17, 0);

        // add 2nd and 3rd row
        for (int i = 0; i < 2; i++) {
            add(buttons.get(index++), 0, 1 + i);
            add(buttons.get(index++), 1, 1 + i);
            for (int j = 12; j < 18; j++) {
                add(buttons.get(index++), j, 1 + i);
            }
        }
        // add 4th and 5th row
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 18; j++) {
                add(buttons.get(index++), j, 2 + i);
            }
        }

        // add 1st and 2nd column in 6th row
        add(buttons.get(index++), 0, 5);
        add(buttons.get(index), 1, 5);

        // add a placeholder for lanthanides
        add(new ElementButton("La-Lu"), 2, 5);

        int lanthanides = Element.LA.atomicNumber - 1;
        int actinides = Element.AC.atomicNumber - 1;
        int laCount = Element.HF.atomicNumber - Element.LA.atomicNumber;
        index = lanthanides + laCount;
        // add the rest of the 6th row
        for (int i = index; i < actinides - 2; i++) {
            add(buttons.get(i), i - index + 3, 5);
        }

        // repeat the process for actinedes
        index = actinides - 2;
        add(buttons.get(index++), 0, 6);
        add(buttons.get(index), 1, 6);
        add(new ElementButton("Ac-Lr"), 2, 6);
        index = actinides + laCount;
        for (int i = index; i < buttons.size(); i++) {
            add(buttons.get(i), i - index + 3, 6);
        }
    }

    private Node createLanthActPane() {
        Label lLabel = new Label("LANTHANIDE");
        Label aLabel = new Label("ACTINIDE");
        lLabel.setPadding(new Insets(3, 0, 0, 0));
        aLabel.setPadding(new Insets(0, 0, 0, 0));
        GridPane.setVgrow(lLabel, Priority.ALWAYS);
        GridPane.setVgrow(aLabel, Priority.ALWAYS);
        add(lLabel, 0, 8, 3, 1);
        add(aLabel, 0, 9, 3, 1);

        int size = getFontSize(lLabel.getText(), lLabel.getFont(), 3 * BTN_SIZE);
        size = (int) Math.min(BTN_SIZE * 0.9, size);
        Font font = Font.font(lLabel.getFont().getFamily(), size);
        lLabel.setFont(font);
        aLabel.setFont(font);

        GridPane lanthActPane = new GridPane();
        lanthActPane.setPadding(new Insets(5, 0, 0, 0));
        lanthActPane.setVgap(0);
        lanthActPane.setHgap(0);
        lanthActPane.setAlignment(Pos.CENTER_LEFT);
        int lanthanides = Element.LA.atomicNumber - 1;
        int actinides = Element.AC.atomicNumber - 1;
        int laCount = Element.HF.atomicNumber - Element.LA.atomicNumber;
        for (int i = lanthanides; i < lanthanides + laCount; i++) {
            lanthActPane.add(buttons.get(i), i - lanthanides, 0);
        }
        for (int i = actinides; i < actinides + laCount; i++) {
            lanthActPane.add(buttons.get(i), i - actinides, 1);
        }
        return lanthActPane;
    }

    private Node createIsotopePanel() {
        GridPane isotopePanel = new GridPane();
        isotopeButton = new Button("---");
        isotopeButton.setCursor(Cursor.HAND);
        isotopeButton.setOnAction(e -> selectedBaseLevelProperty().setValue(internalIsotopeProperty().getValue()));
        isotopeButton.setPadding(new Insets(1, 2, 1, 2));
        isotopeButton.setPrefHeight(32);
        isotopeButton.setMinWidth(0);
        isotopeButton.setMaxWidth(Double.MAX_VALUE);
        // Max font is 20, which is good for Windows, but too large for linux
        // max width = idealWidth (110) - (left + right) padding
        int siz = getFontSize(Isotope.of(Element.MD, 157, 100).getPresentationName(), Font.font(20), (int) 106);
        isotopeButton.setFont(Font.font(siz));
        isotopeButton.disableProperty().bind(disableProperty());
        animatedStyle += "-fx-font-size: " + siz + "; ";

        neutronSpinner = new TinySpinner(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 180));
        neutronSpinner.disableProperty().bind(disableProperty());
        neutronSpinner.setPrefSize(2.2 * BTN_SIZE, BTN_SIZE);
        neutronSpinner.valueProperty().addListener((a, o, n) -> {
            Isotope iso = internalIsotopeProperty().getValue();
            if (iso != null) {
                internalIsotopeProperty().setValue(Isotope.of(iso.element, n, iso.charge));
            }
        });
        chargeSpinner = new TinySpinner(new SpinnerValueFactory.IntegerSpinnerValueFactory(-100, 112, 0));
        chargeSpinner.disableProperty().bind(disableProperty());
        chargeSpinner.setPrefSize(2.2 * BTN_SIZE, BTN_SIZE);
        chargeSpinner.valueProperty().addListener((a, o, n) -> {
            Isotope iso = internalIsotopeProperty().getValue();
            if (iso != null) {
                internalIsotopeProperty().setValue(Isotope.of(iso.element, iso.neutrons, n));
            }
        });

        isotopeCombo = new ComboBox<>();
        isotopeCombo.disableProperty().bind(disableProperty());
        isotopeCombo.setPadding(new Insets(1, 0, 1, 0));
        isotopeCombo.setEditable(false);
        isotopeCombo.setMaxWidth(Double.MAX_VALUE);
        isotopeCombo.setMinWidth(0);
        isotopeCombo.setCellFactory(c -> new ComboCell());
        isotopeCombo.setButtonCell(new ComboCell());
        isotopeCombo.setStyle("-fx-font-size: 14;");
        isotopeCombo.selectionModelProperty().get().selectedItemProperty()
            .addListener((a, o, n) -> internalIsotopeProperty().setValue(n));
        // adjust the height to always be the same as the button height
        isotopeCombo.prefHeightProperty().bind(isotopeButton.heightProperty());
        isotopeCombo.maxHeightProperty().bind(isotopeButton.heightProperty());

        neutronLabel = new Label("Neutrons:");
        chargeLabel = new Label("Charge:");

        Font font = Font.font(neutronLabel.getFont().getFamily(),
            getFontSize(neutronLabel.getText(), neutronLabel.getFont(), (int) neutronSpinner.getPrefWidth()));
        neutronLabel.setFont(font);
        chargeLabel.setFont(font);
        neutronSpinner.getEditor().setFont(font);
        chargeSpinner.getEditor().setFont(font);

        setGridConstraints(isotopeCombo, true, false, HPos.LEFT, VPos.BOTTOM, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(isotopeButton, true, false, HPos.LEFT, VPos.BOTTOM, Priority.ALWAYS, Priority.NEVER);
        GridPane.setMargin(isotopeButton, new Insets(3, 0, 0, 0));

        isotopePanel.setPadding(new Insets(0, 5, 5, 5));
        isotopePanel.setVgap(1);
        isotopePanel.setHgap(5);
        isotopePanel.add(neutronLabel, 0, 0, 1, 1);
        isotopePanel.add(chargeLabel, 1, 0, 1, 1);
        isotopePanel.add(neutronSpinner, 0, 1, 1, 1);
        isotopePanel.add(chargeSpinner, 1, 1, 1, 1);
        isotopePanel.add(isotopeCombo, 0, 0, 2, 2);
        isotopePanel.add(isotopeButton, 2, 0, 1, 2);
        isotopeCombo.setVisible(false);

        setGridConstraints(isotopePanel, true, false, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.NEVER);

        isotopePanel.setMaxWidth(10 * BTN_SIZE);
        isotopePanel.setMinWidth(0);
        return isotopePanel;
    }

    private void showOnlyAvailable(final boolean only) {
        buttons.forEach(b -> b.setDisable(only));
        if (only) {
            availableBaseLevelsProperty().get().forEach(i -> buttons.get(i.element.atomicNumber - 1).setDisable(false));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#setShowOnlyAvailable (boolean)
     */
    @Override
    public void setShowOnlyAvailable(boolean showOnlyAvailable) {
        if (this.showOnlyAvailable != showOnlyAvailable) {
            this.showOnlyAvailable = showOnlyAvailable;
            neutronSpinner.setVisible(!showOnlyAvailable);
            chargeSpinner.setVisible(!showOnlyAvailable);
            chargeLabel.setVisible(!showOnlyAvailable);
            neutronLabel.setVisible(!showOnlyAvailable);
            isotopeCombo.setVisible(showOnlyAvailable);
            showOnlyAvailable(this.showOnlyAvailable);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser# availableBaseLevelsProperty()
     */
    public ObjectProperty<List<Isotope>> availableBaseLevelsProperty() {
        if (availableIsotopesProperty == null) {
            availableIsotopesProperty = new SimpleObjectProperty<>(new ArrayList<>());
            availableIsotopesProperty.addListener((a, o, n) -> {
                if (showOnlyAvailable) {
                    showOnlyAvailable(true);
                }
            });
        }
        return availableIsotopesProperty;
    }

    private void setSelectedElement(String element) {
        Element e = Element.valueOf(element.toUpperCase(Locale.UK));
        int n = e.commonNeutrons;
        int c = e.commonCharge;
        if (showOnlyAvailable) {
            final List<Isotope> isotopes = new ArrayList<>();
            availableBaseLevelsProperty().get().forEach(a -> {
                if (a.element == e) {
                    isotopes.add(a);
                }
            });
            isotopeCombo.getItems().setAll(isotopes);
            if (!isotopes.isEmpty()) {
                n = isotopes.get(0).neutrons;
                c = isotopes.get(0).charge;
                isotopeCombo.selectionModelProperty().get().select(0);
            }
        } else {
            for (Isotope a : availableBaseLevelsProperty().get()) {
                if (a.element == e) {
                    n = a.neutrons;
                    c = a.charge;
                    break;
                }
            }
        }
        internalIsotopeProperty().setValue(Isotope.of(e, n, c));
    }

    private Property<Isotope> internalIsotopeProperty() {
        if (internalIsotopeProperty == null) {
            internalIsotopeProperty = new SimpleObjectProperty<Isotope>(this, "internalSelectedIsotope", null) {
                @Override
                public void set(Isotope isotope) {
                    Isotope selectedIsotope = get();
                    if (selectedIsotope != null && selectedIsotope.equals(isotope)) {
                        return;
                    }
                    if (selectedIsotope != null) {
                        buttons.get(selectedIsotope.element.atomicNumber - 1)
                            .setStyle(selectedIsotope.element.getStyle());
                        buttons.get(selectedIsotope.element.atomicNumber - 1).setPrefSize(BTN_SIZE, BTN_SIZE);
                    }
                    if (isotope != null) {
                        buttons.get(isotope.element.atomicNumber - 1).setStyle(SELECTED_STYLE);
                        buttons.get(isotope.element.atomicNumber - 1).setPrefSize(BTN_SIZE - 2, BTN_SIZE - 2);
                    }
                    super.set(isotope);
                }
            };
            internalIsotopeProperty.addListener((a, o, n) -> {
                if (n != null) {
                    isotopeButton.setText(n.getPresentationName());

                    IntegerSpinnerValueFactory factory = (IntegerSpinnerValueFactory) chargeSpinner
                        .valueFactoryProperty().get();
                    factory.setMax(n.element.atomicNumber);

                    neutronSpinner.valueFactoryProperty().get().setValue(n.neutrons);
                    factory.setValue(n.charge);

                    if (showOnlyAvailable) {
                        isotopeCombo.selectionModelProperty().get().select(n);
                    }

                    Isotope iso = selectedBaseLevelProperty().getValue();
                    if (!n.equals(iso) && animation.getStatus() != Status.RUNNING) {
                        isotopeButton.setStyle(animatedStyle);
                        animation.play();
                    } else if (n.equals(iso)) {
                        animation.pause();
                        animation.jumpTo(Duration.seconds(0));
                        animation.stop();
                        isotopeButton.setStyle(null);
                    }
                }
            });
        }
        return internalIsotopeProperty;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser# selectedBaseLevelProperty()
     */
    @Override
    public Property<Isotope> selectedBaseLevelProperty() {
        if (selectedBaseLevelProperty == null) {
            selectedBaseLevelProperty = new SimpleObjectProperty<>(this, "selectedBaseLevel", null);
            selectedBaseLevelProperty.addListener((a, o, n) -> {
                internalIsotopeProperty().setValue(n);
                animation.pause();
                animation.jumpTo(Duration.seconds(0));
                animation.stop();
                isotopeButton.setStyle(null);
            });
        }
        return selectedBaseLevelProperty;
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
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#getTitleFor(java. util.Optional, java.util.Optional)
     */
    @Override
    public String getTitleFor(Optional<Isotope> bl, Optional<String> branch) {
        if (bl.isPresent()) {
            if (branch.isPresent()) {
                return "Elements for " + branch.get() + " (" + bl.get().element.fullName + ")";
            } else {
                return "Elements (" + bl.get().element.fullName + ")";
            }
        } else {
            return "Elements";
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#transform(java.util. List)
     */
    @Override
    public List<Isotope> transform(List<? extends BaseLevel> list) {
        return Collections.unmodifiableList(list.stream().map(e -> Isotope.ofFlat(e.getStorageName()))
            .filter(e -> e != null).collect(Collectors.toList()));

    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#getReadableName()
     */
    @Override
    public String getReadableName() {
        return "Periodic Table";
    }

    /**
     * Returns the font size that is small enough to show the given text in a component, which maximum width is given by
     * the <code>maxWidth</code> parameters.
     *
     * @param text the text to fit into maxWidth
     * @param font the initial font size and family
     * @param maxWidth the maximum allowed width of the text
     * @return the font size
     */
    private static int getFontSize(String text, Font font, int maxWidth) {
        Text mtext = new Text(text);
        mtext.setFont(font);
        double size = mtext.getLayoutBounds().getWidth();
        int fontSize = (int) font.getSize();
        while (size > maxWidth && fontSize > 1) {
            fontSize--;
            mtext.setFont(Font.font(font.getFamily(), fontSize));
            size = mtext.getLayoutBounds().getWidth();
        }
        return fontSize;
    }
}
