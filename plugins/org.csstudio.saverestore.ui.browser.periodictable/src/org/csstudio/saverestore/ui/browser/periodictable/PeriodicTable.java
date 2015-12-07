package org.csstudio.saverestore.ui.browser.periodictable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.ui.browser.BaseLevelBrowser;

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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
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

    private static final int BTN_SIZE = 25;
    private static final String SELECTED_STYLE = "-fx-background-color: #8080FF; -fx-text-fill: white; "
            + "-fx-font-weight: bold; -fx-font-size: 9; "
            + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1);";

    private static final String ANIMATED_STYLE = "-fx-background-color: #FF8080; -fx-text-fill: white; "
            + "-fx-font-size: 20; -fx-font-weight: bold; "
            + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1);";

    private ObjectProperty<Isotope> isotopeProperty;
    private ObjectProperty<Isotope> internalIsotopeProperty;
    private ObjectProperty<List<Isotope>> availableIsotopesProperty;

    private List<Button> buttons;

    private Button isotopeButton;
    private FadeTransition animation;
    private Spinner<Integer> neutronSpinner;
    private Spinner<Integer> chargeSpinner;
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

        //label + gap + spinner + gap + button + padding + metals + gap + lant & act
        int height = 17 + 1 + BTN_SIZE + 1 + 32 + 5 + BTN_SIZE * 4 + 5 + BTN_SIZE * 2;
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
            Button b = new Button(elements[i].symbol);
            b.setId(elements[i].symbol);
            Font font = b.getFont();
            b.setFont(Font.font(font.getFamily(), 10));
            b.setPadding(new Insets(2, 2, 2, 2));
            b.setPrefSize(BTN_SIZE, BTN_SIZE);
            b.setOnAction((e) -> setSelectedElement(((Button) e.getSource()).getId()));
            b.setStyle(elements[i].getStyle());
            GridPane.setHalignment(b, HPos.CENTER);
            GridPane.setValignment(b, VPos.BOTTOM);
            GridPane.setVgrow(b, Priority.NEVER);
            GridPane.setFillHeight(b, false);
            b.setCursor(Cursor.HAND);
            b.setTooltip(new Tooltip(elements[i].fullName));
            buttons.add(b);
        }
        for (int i = 0; i < 2; i++) {
            Button b = buttons.get(i);
            GridPane.setVgrow(b, Priority.ALWAYS);
            GridPane.setMargin(b, new Insets(0,0,0,0));
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
            add(buttons.get(index++), 12, 1 + i);
            add(buttons.get(index++), 13, 1 + i);
            add(buttons.get(index++), 14, 1 + i);
            add(buttons.get(index++), 15, 1 + i);
            add(buttons.get(index++), 16, 1 + i);
            add(buttons.get(index++), 17, 1 + i);
        }
        // add 4th and 5th row
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 18; j++) {
                add(buttons.get(index++), j, 2 + i);
            }
        }

        // add 1st and 2nd column in 6th row
        add(buttons.get(index++), 0, 5);
        add(buttons.get(index++), 1, 5);

        // add a placeholder for lanthanides
        Button lanthanids = new Button("La-Lu");
        lanthanids.setPrefSize(BTN_SIZE, BTN_SIZE);
        lanthanids.setPadding(new Insets(2, 2, 2, 2));
        lanthanids.setDisable(true);
        Font font = lanthanids.getFont();
        font = Font.font(font.getFamily(), 8);
        lanthanids.setFont(font);
        add(lanthanids, 2, 5);

        int LANTHANIDES = Element.LA.atomicNumber - 1;
        int ACTINIDES = Element.AC.atomicNumber - 1;
        int LA_COUNT = Element.HF.atomicNumber - Element.LA.atomicNumber;
        index = LANTHANIDES + LA_COUNT;
        // add the rest of the 6th row
        for (int i = index; i < ACTINIDES - 2; i++) {
            add(buttons.get(i), i - index + 3, 5);
        }

        // repeat the process for actinedes
        index = ACTINIDES - 2;
        add(buttons.get(index++), 0, 6);
        add(buttons.get(index++), 1, 6);
        Button actinides = new Button("Ac-Lr");
        actinides.setPrefSize(BTN_SIZE, BTN_SIZE);
        actinides.setPadding(new Insets(2, 2, 2, 2));
        actinides.setDisable(true);
        actinides.setFont(font);
        add(actinides, 2, 6);
        index = ACTINIDES + LA_COUNT;
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

        GridPane lanthActPane = new GridPane();
        lanthActPane.setPadding(new Insets(5, 0, 0, 0));
        lanthActPane.setVgap(0);
        lanthActPane.setHgap(0);
        lanthActPane.setAlignment(Pos.CENTER_LEFT);
        int LANTHANIDES = Element.LA.atomicNumber - 1;
        int ACTINIDES = Element.AC.atomicNumber - 1;
        int LA_COUNT = Element.HF.atomicNumber - Element.LA.atomicNumber;
        for (int i = LANTHANIDES; i < LANTHANIDES + LA_COUNT; i++) {
            lanthActPane.add(buttons.get(i), i - LANTHANIDES, 0);
        }
        for (int i = ACTINIDES; i < ACTINIDES + LA_COUNT; i++) {
            lanthActPane.add(buttons.get(i), i - ACTINIDES, 1);
        }
        return lanthActPane;
    }

    private Node createIsotopePanel() {
        GridPane isotopePanel = new GridPane();
        isotopeButton = new Button("---");
        isotopeButton.setCursor(Cursor.HAND);
        isotopeButton.setOnAction((e) -> baseLevelProperty().setValue(internalIsotopeProperty().getValue()));
        isotopeButton.setPadding(new Insets(1, 2, 1, 2));
        isotopeButton.setPrefHeight(32);
        isotopeButton.setPrefWidth(110);
        isotopeButton.setFont(Font.font(20));
        isotopeButton.disableProperty().bind(disableProperty());

        neutronSpinner = new Spinner<>();
        neutronSpinner.disableProperty().bind(disableProperty());
        neutronSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 180));
        neutronSpinner.setEditable(true);
        neutronSpinner.setPrefSize(2.4 * BTN_SIZE, BTN_SIZE);
        neutronSpinner.valueProperty().addListener((a, o, n) -> {
            Isotope iso = internalIsotopeProperty().getValue();
            if (iso != null) {
                internalIsotopeProperty().setValue(Isotope.of(iso.element, n, iso.charge));
            }
        });
        chargeSpinner = new Spinner<>();
        chargeSpinner.disableProperty().bind(disableProperty());
        chargeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-100, 112, 0));
        chargeSpinner.setEditable(true);
        chargeSpinner.setPrefSize(2.4 * BTN_SIZE, BTN_SIZE);
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
        isotopeCombo.setPrefSize(4.5 * BTN_SIZE + 5, BTN_SIZE);
        isotopeCombo.setCellFactory(c -> new ListCell<Isotope>() {
            protected void updateItem(Isotope item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getPresentationName());
                }
            }
        });
        isotopeCombo.setButtonCell(new ListCell<Isotope>() {
            protected void updateItem(Isotope item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getPresentationName());
                }
            };
        });
        isotopeCombo.setStyle("-fx-font-size: 14");
        isotopeCombo.selectionModelProperty().get().selectedItemProperty()
                .addListener((a, o, n) -> internalIsotopeProperty().setValue(n));

        neutronLabel = new Label("Neutrons:");
        chargeLabel = new Label("Charge:");

        GridPane.setVgrow(isotopeCombo, Priority.NEVER);
        GridPane.setHgrow(isotopeCombo, Priority.ALWAYS);
        GridPane.setValignment(isotopeCombo, VPos.BOTTOM);
        GridPane.setHalignment(isotopeCombo, HPos.CENTER);
        GridPane.setFillWidth(isotopeCombo, false);

        GridPane.setVgrow(isotopeButton, Priority.NEVER);
        GridPane.setHgrow(isotopeButton, Priority.ALWAYS);
        GridPane.setValignment(isotopeButton, VPos.BOTTOM);
        GridPane.setHalignment(isotopeButton, HPos.CENTER);
        GridPane.setFillWidth(isotopeButton, true);
        GridPane.setMargin(isotopeButton, new Insets(3,0,0,0));

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

        GridPane.setHgrow(isotopePanel, Priority.ALWAYS);
        GridPane.setVgrow(isotopePanel, Priority.NEVER);
        GridPane.setValignment(isotopePanel, VPos.TOP);
        GridPane.setHalignment(isotopePanel, HPos.CENTER);
        GridPane.setFillWidth(isotopePanel, false);
        return isotopePanel;
    }

    private void showOnlyAvailable(boolean only) {
        if (only) {
            buttons.forEach(b -> b.setDisable(true));
            availableBaseLevelsProperty().get().forEach(i -> buttons.get(i.element.atomicNumber - 1).setDisable(false));
        } else {
            buttons.forEach(b -> b.setDisable(false));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#setShowOnlyAvailable(boolean)
     */
    @Override
    public void setShowOnlyAvailable(boolean showOnlyAvailable) {
        if (this.showOnlyAvailable == showOnlyAvailable)
            return;
        this.showOnlyAvailable = showOnlyAvailable;
        neutronSpinner.setVisible(!showOnlyAvailable);
        chargeSpinner.setVisible(!showOnlyAvailable);
        chargeLabel.setVisible(!showOnlyAvailable);
        neutronLabel.setVisible(!showOnlyAvailable);
        isotopeCombo.setVisible(showOnlyAvailable);
        showOnlyAvailable(this.showOnlyAvailable);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#availableBaseLevelsProperty()
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
        Element e = Element.valueOf(element.toUpperCase());
        int n = e.commonNeutrons;
        int c = e.commonCharge;
        if (showOnlyAvailable) {
            final List<Isotope> isotopes = new ArrayList<>();
            availableBaseLevelsProperty().get().forEach(a -> {
                if (a.element == e)
                    isotopes.add(a);
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

                    Isotope iso = baseLevelProperty().getValue();
                    if (!n.equals(iso) && animation.getStatus() != Status.RUNNING) {
                        isotopeButton.setStyle(ANIMATED_STYLE);
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
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#baseLevelProperty()
     */
    @Override
    public Property<Isotope> baseLevelProperty() {
        if (isotopeProperty == null) {
            isotopeProperty = new SimpleObjectProperty<Isotope>(this, "selectedIsotope", null);
            isotopeProperty.addListener((a, o, n) -> {
                internalIsotopeProperty().setValue(n);
                animation.pause();
                animation.jumpTo(Duration.seconds(0));
                animation.stop();
                isotopeButton.setStyle(null);
            });
        }
        return isotopeProperty;
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
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#getTitleFor(java.util.Optional, java.util.Optional)
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
     * @see org.csstudio.saverestore.ui.browser.BaseLevelBrowser#transform(java.util.List)
     */
    @Override
    public List<Isotope> transform(List<? extends BaseLevel> list) {
        List<Isotope> ret = new ArrayList<>(list.size());
        list.forEach(e -> ret.add(Isotope.of(e.getStorageName())));
        return Collections.unmodifiableList(ret);
    }
}
