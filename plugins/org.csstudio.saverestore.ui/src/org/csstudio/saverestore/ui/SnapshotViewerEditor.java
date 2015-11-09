package org.csstudio.saverestore.ui;

import java.util.Arrays;
import java.util.List;

import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.Snapshot;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.VNoData;
import org.csstudio.saverestore.VSnapshot;
import org.csstudio.saverestore.ui.util.ComboInputDialog;
import org.csstudio.saverestore.ui.util.FXEditorPart;
import org.csstudio.saverestore.ui.util.SnapshotDataFormat;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 *
 * <code>SnapshotViewerEditor</code> is an {@link IEditorPart} implementation for displaying, creating, comparing,
 * and restoring snapshots. It provides a table that displays the stored values (together with the alarm data and
 * timestamp) and live data. In comparison view only the values are displayed. At the top of the editor buttons
 * for taking, saving and restoring the snapshot are located.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotViewerEditor extends FXEditorPart {

    /**
     *
     * <code>VTypeCellEditor</code> is an editor type for {@link VType} or {@link VTypePair},
     * which allows editing the value as a string.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     * @param <T> {@link VType} or {@link VTypePair}
     */
    private static class VTypeCellEditor<T> extends TextFieldTableCell<TableEntry, T> {
        VTypeCellEditor() {
            setConverter(new StringConverter<T>() {
                @Override
                public String toString(T object) {
                    if (object == null) {
                        return "---";
                    } else if (object instanceof VType) {
                        return Utilities.valueToString((VType) object);
                    } else if (object instanceof VTypePair) {
                        return Utilities.valueToString(((VTypePair) object).value);
                    } else {
                        return object.toString();
                    }
                }

                @SuppressWarnings("unchecked")
                @Override
                public T fromString(String string) {
                    T item = getItem();
                    if (string == null) {
                        return null;
                    } else if (item instanceof VType) {
                        return (T) Utilities.valueFromString(string, (VType) item);
                    } else if (item instanceof VTypePair) {
                        VTypePair t = (VTypePair) item;
                        if (t.value instanceof VNoData) {
                            return (T) new VTypePair(t.base, Utilities.valueFromString(string, t.base));
                        } else {
                            return (T) new VTypePair(t.base, Utilities.valueFromString(string, t.value));
                        }
                    } else {
                        return null;
                    }
                }
            });
            setTooltip(new Tooltip());
        }

        /*
         * (non-Javadoc)
         * @see javafx.scene.control.cell.TextFieldTableCell#updateItem(java.lang.Object, boolean)
         */
        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText("---");
                setStyle("");
                getTooltip().setText(null);
            } else {
                if (item instanceof VType) {
                    setText(Utilities.valueToString((VType) item));
                } else if (item instanceof VTypePair) {
                    setText(Utilities.valueToCompareString(((VTypePair) item).value, ((VTypePair) item).base));
                }
                getTooltip().setText(item.toString());
            }
        }
    }

    /**
     * <code>EditorTableColumn</code> is the common table column implementation, which can also provide
     * the tooltip.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     * @param <T> the type of the values displayed by this column
     */
    private static class EditorTableColumn<T> extends TableColumn<TableEntry, T> {
        EditorTableColumn(String text, String tooltip, int minWidth) {
            setup(text, tooltip, minWidth, -1, true);
        }

        EditorTableColumn(String text, String tooltip, int minWidth, int prefWidth, boolean resizable) {
            setup(text, tooltip, minWidth, prefWidth, resizable);
        }

        private void setup(String text, String tooltip, int minWidth, int prefWidth, boolean resizable) {
            Label label = new Label(text);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setTooltip(new Tooltip(tooltip));
            setGraphic(label);
            if (minWidth != -1) {
                setMinWidth(minWidth);
            }
            if (prefWidth != -1) {
                setPrefWidth(prefWidth);
            }
            setResizable(resizable);
        }
    }

    public static final String ID = "org.csstudio.saverestore.ui.editor.snapshotviewer";

    private static final String ANIMATED_STYLE = "-fx-background-color: #FF8080; -fx-text-fill: white; "
            + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1);";

    private Scene scene;
    private BorderPane contentPane;
    private TableView<TableEntry> table;
    private SnapshotViewerController model;
    private TextArea commentField;
    private TextField dateField;
    private TextField creatorField;
    private Button takeSnapshotButton;
    private Button restoreSnapshotButton;
    private Button saveSnapshotButton;
    private FadeTransition animation;

    /**
     * Constructs a new editor.
     */
    public SnapshotViewerEditor() {
        model = new SnapshotViewerController(this);
    }

    private void init() {
        animation = new FadeTransition(Duration.seconds(0.5),saveSnapshotButton);
        animation.setAutoReverse(true);
        animation.setFromValue(1.0);
        animation.setToValue(0.6);
        animation.setCycleCount(FadeTransition.INDEFINITE);
        model.snapshotSaveableProperty().addListener((a,o,n) -> {
            if (!n) {
                animation.pause();
                animation.jumpTo(Duration.seconds(0));
                animation.stop();
                saveSnapshotButton.setStyle(null);
            } else if (animation.getStatus() != Status.RUNNING){
                saveSnapshotButton.setStyle(ANIMATED_STYLE);
                animation.play();
            }
            firePropertyChange(PROP_DIRTY);
        });
        if (model.snapshotSaveableProperty().get()) {
            saveSnapshotButton.setStyle(ANIMATED_STYLE);
            animation.play();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        List<VSnapshot> snapshots = null;
        do {
            snapshots = model.getSnapshots(true);
            if (snapshots.isEmpty()) {
                break;
            } else if (snapshots.size() == 1) {
                model.saveSnapshot(snapshots.get(0));
            } else {
                String[] items = new String[snapshots.size()];
                for (int i = 0; i < snapshots.size(); i++) {
                    items[i] = snapshots.get(i).toString();
                }
                ComboInputDialog dialog = new ComboInputDialog(getSite().getShell(),"Select Snapshot",
                        "Select the snapshot that you wish to save:",items[0],items,null);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    final String val = dialog.getValue();
                    snapshots.forEach(x -> {
                        if (val.equals(x.toString())) {
                            model.saveSnapshot(x);
                        }
                    });
                } else {
                    break;
                }

    //            ChoiceDialog<VSnapshot> dialog = new ChoiceDialog<>(snapshots.get(snapshots.size()-1), snapshots);
    //            dialog.initOwner(scene.getWindow());
    //            dialog.initModality(Modality.APPLICATION_MODAL);
    //            dialog.setTitle("Select Snapshot");
    //            dialog.setHeaderText("Select the snapshot that you wish to save:");
    //            Optional<VSnapshot> result = dialog.showAndWait();
    //            result.ifPresent(p -> model.saveSnapshot(p));
            }
            firePropertyChange(PROP_DIRTY);
        } while(!snapshots.isEmpty());
        monitor.done();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        doSave(new NullProgressMonitor());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        VSnapshot snapshot = input.getAdapter(VSnapshot.class);
        if (snapshot != null) {
            setSnapshot(snapshot);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        model.dispose();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return model.snapshotSaveableProperty().get();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#createFxScene()
     */
    @Override
    protected Scene createFxScene() {
        contentPane = new BorderPane();
        GridPane topPane = new GridPane();
        Node top = createTopPane();
        Node bottom = createButtonPane();
        GridPane.setHgrow(top, Priority.ALWAYS);
        GridPane.setHgrow(bottom, Priority.NEVER);
        GridPane.setFillHeight(bottom, true);
        topPane.add(top,0,0);
        topPane.add(bottom,1,0);

        contentPane.setTop(topPane);
//        contentPane.setBottom(createBottomPane());
        scene = new Scene(contentPane);
        init();
        return scene;
    }

    private Node createTopPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5,5,5,5));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.TOP_LEFT);
        commentField = new TextArea();
        commentField.setEditable(false);
        commentField.setWrapText(true);
        commentField.setPrefWidth(300);
        commentField.setPrefRowCount(2);
        GridPane.setVgrow(commentField, Priority.ALWAYS);
        GridPane.setFillHeight(commentField, true);
        creatorField = new TextField();
        creatorField.setEditable(false);
        creatorField.setPrefWidth(150);
        dateField = new TextField();
        dateField.setEditable(false);
        dateField.setPrefWidth(150);

        grid.add(new Label("Comment:"), 0, 0);
        grid.add(commentField, 1,0,1,2);
        grid.add(new Label("Creator:"), 2,0);
        grid.add(new Label("Timestamp:"), 2, 1);
        grid.add(creatorField, 3, 0);
        grid.add(dateField, 3, 1);
        return grid;
    }

    private Node createButtonPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5,5,5,5));
        grid.setHgap(5);

        takeSnapshotButton = new Button();
        VBox box = new VBox();
        box.getChildren().addAll(new Label("Take"),new Label("Snapshot"));
        box.setAlignment(Pos.CENTER);
        takeSnapshotButton.setGraphic(box);
        takeSnapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        takeSnapshotButton.setTooltip(new Tooltip("Take a new snapshot (locally store the live values)"));
        takeSnapshotButton.setOnAction(e -> model.takeSnapshot());

        saveSnapshotButton = new Button();
        box = new VBox();
        box.getChildren().addAll(new Label("Save"),new Label("Snapshot"));
        box.setAlignment(Pos.CENTER);
        saveSnapshotButton.setGraphic(box);
        saveSnapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        saveSnapshotButton.setTooltip(new Tooltip("Save the snapshot values as a new revision"));

        saveSnapshotButton.setOnAction(e -> doSaveAs());

        saveSnapshotButton.disableProperty().bind(model.snapshotSaveableProperty().not());

        restoreSnapshotButton = new Button("Restore");
        restoreSnapshotButton.setTooltip(new Tooltip("Set the stored values to PVs"));
        restoreSnapshotButton.setOnAction(e -> {
            List<VSnapshot> snapshots = model.getSnapshots(false);
            if (snapshots.isEmpty()) {
                return;
            } else if (snapshots.size() == 1) {
                model.restoreSnapshot(snapshots.get(0));
            } else {
                String[] items = new String[snapshots.size()];
                for (int i = 0; i < snapshots.size(); i++) {
                    items[i] = snapshots.get(i).toString();
                }
                ComboInputDialog dialog = new ComboInputDialog(getSite().getShell(),"Select Snapshot",
                        "Select the snapshot that you wish to use for restoring:",items[0],items,null);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    final String val = dialog.getValue();
                    snapshots.forEach(x -> {
                        if (val.equals(x.toString())) {
                            model.restoreSnapshot(x);
                        }
                    });
                }
//                ChoiceDialog<VSnapshot> dialog = new ChoiceDialog<>(snapshots.get(0), snapshots);
//                dialog.initOwner(scene.getWindow());
//                dialog.initModality(Modality.APPLICATION_MODAL);
//                dialog.setTitle("Select Snapshot");
//                dialog.setHeaderText("Select the snapshot that you wish to use for restoring:");
//                Optional<VSnapshot> result = dialog.showAndWait();
//                result.ifPresent(p -> model.restoreSnapshot(p));
            }
        });
        restoreSnapshotButton.disableProperty().bind(model.snapshotRestorableProperty().not());

        restoreSnapshotButton.setMaxHeight(Integer.MAX_VALUE);
        saveSnapshotButton.setMaxHeight(Integer.MAX_VALUE);
        takeSnapshotButton.setMaxHeight(Integer.MAX_VALUE);
        GridPane.setHgrow(restoreSnapshotButton, Priority.ALWAYS);
        GridPane.setHgrow(takeSnapshotButton, Priority.NEVER);
        GridPane.setHgrow(saveSnapshotButton, Priority.NEVER);
        GridPane.setVgrow(restoreSnapshotButton, Priority.ALWAYS);
        GridPane.setVgrow(takeSnapshotButton, Priority.ALWAYS);
        GridPane.setVgrow(saveSnapshotButton, Priority.ALWAYS);
        GridPane.setFillHeight(restoreSnapshotButton, true);
        GridPane.setFillHeight(takeSnapshotButton, true);
        GridPane.setFillHeight(saveSnapshotButton, true);
        GridPane.setFillWidth(restoreSnapshotButton, true);
        GridPane.setFillWidth(takeSnapshotButton, true);
        GridPane.setFillWidth(saveSnapshotButton, true);
        grid.add(restoreSnapshotButton, 0, 0);
        grid.add(takeSnapshotButton, 1, 0);
        grid.add(saveSnapshotButton, 2, 0);

        return grid;
    }

    private TableView<TableEntry> createTableForSingleSnapshot() {
        TableView<TableEntry> table = new TableView<>();
        TableColumn<TableEntry, Boolean> selectedColumn = new EditorTableColumn<>(
                "", "Include this PV when restoring values", 30, 30, false);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setEditable(true);
        selectedColumn
                .setOnEditCommit((e) -> ((TableEntry) e.getTableView().getItems().get(e.getTablePosition().getRow()))
                        .selectedProperty().setValue(e.getNewValue()));

        TableColumn<TableEntry, Integer> idColumn = new EditorTableColumn<>(
                "#", "The order number of the PV in the beamline set", 30, 30, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TableEntry, String> pvNameColumn = new EditorTableColumn<>(
                "PV", "The name of the PV", 170);
        pvNameColumn.setCellValueFactory(new PropertyValueFactory<>("pvName"));

        TableColumn<TableEntry, Timestamp> timestampColumn = new EditorTableColumn<>(
                "Timestamp", "Timestamp of the value when the snapshot was taken", 120, 135, true);
        timestampColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, Timestamp>("timestamp"));
        timestampColumn.setCellFactory(c -> new TableCell<TableEntry, Timestamp>() {
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(Utilities.timestampToString(item));
                }
            }
        });
        timestampColumn.setPrefWidth(135);

        TableColumn<TableEntry, String> statusColumn = new EditorTableColumn<>(
                "Status", "Alarm status of the PV when the snapshot was taken", 100, 100, true);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<TableEntry, AlarmSeverity> severityColumn = new EditorTableColumn<>(
                "Severity", "Alarm severity of the PV when the snapshot was taken", 80, 80, false);
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));

        TableColumn<TableEntry, VType> storedValueColumn = new EditorTableColumn<>(
                "Stored Value", "PV value when the snapshot was taken", 100);
        storedValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        storedValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        storedValueColumn.setEditable(true);
        storedValueColumn.setOnEditCommit((e) -> {
            ((TableEntry) e.getTableView().getItems().get(e.getTablePosition().getRow()))
                    .valueProperty().setValue(e.getNewValue());
        });

        TableColumn<TableEntry, VType> liveValueColumn = new EditorTableColumn<>(
                "Live Value", "Current PV value", 100);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        liveValueColumn.setEditable(false);

        List<TableColumn<TableEntry, ?>> list = Arrays.asList(selectedColumn, idColumn, pvNameColumn, timestampColumn,
                statusColumn, severityColumn, storedValueColumn, liveValueColumn);
        table.getColumns().addAll(list);

        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configureTableForDnD(table);
        return table;
    }

    private void configureTableForDnD(TableView<TableEntry> table) {
        table.setOnDragOver(e -> {
            if (e.getGestureSource() != table && e.getDragboard().hasContent(SnapshotDataFormat.INSTANCE)) {
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();
        });

        table.setOnDragDropped(e -> {
            if (e.getDragboard().hasContent(SnapshotDataFormat.INSTANCE)) {
                Snapshot s = (Snapshot) e.getDragboard().getContent(SnapshotDataFormat.INSTANCE);
                if (s != null) {
                    Engine.getInstance().execute(() -> {
                        VSnapshot vs = Engine.getInstance().getSelectedDataProvider().provider.getSnapshotContent(s);
                        addSnapshot(vs);
                    });
                }
            }
        });
    }

    private TableView<TableEntry> createTableForMultipleSnapshots(int n) {
        TableView<TableEntry> table = new TableView<>();
        TableColumn<TableEntry, Boolean> selectedColumn = new EditorTableColumn<>(
                "", "Include this PV when restoring values", 30, 30, false);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setEditable(true);
        selectedColumn
                .setOnEditCommit(e -> ((TableEntry) e.getTableView().getItems().get(e.getTablePosition().getRow()))
                        .selectedProperty().setValue(e.getNewValue()));

        TableColumn<TableEntry, Integer> idColumn = new EditorTableColumn<>(
                "#", "The order number of the PV in the beamline set", 30, 30, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TableEntry, String> pvNameColumn = new EditorTableColumn<>(
                "PV", "The name of the PV", 170);
        pvNameColumn.setCellValueFactory(new PropertyValueFactory<>("pvName"));

        TableColumn<TableEntry, VType> liveValueColumn = new EditorTableColumn<>(
                "Live Value", "Current PV value",-1);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        liveValueColumn.setEditable(false);

        TableColumn<TableEntry, ?> storedValueColumn = new EditorTableColumn<>(
                "Stored Values", "PV value when the snapshot was taken", -1);

        TableColumn<TableEntry, VType> baseCol = new EditorTableColumn<>(
                "Base", "PV value when the snapshot was taken", 100);
        baseCol.setCellValueFactory(e -> e.getValue().valueProperty());
        baseCol.setCellFactory(e -> new VTypeCellEditor<>());
        storedValueColumn.getColumns().add(baseCol);

        for (int i = 1; i < n; i++) {
            TableColumn<TableEntry, VTypePair> col = new EditorTableColumn<>(
                    model.getSnapshot(i).toString(), "PV value when the snapshot was taken", 100);
            final int a = i;
            col.setCellValueFactory(e -> e.getValue().compareValueProperty(a));
            col.setCellFactory(e -> new VTypeCellEditor<>());
            col.setEditable(true);
            storedValueColumn.getColumns().add(col);
        }

        List<TableColumn<TableEntry, ?>> list = Arrays.asList(selectedColumn, idColumn, pvNameColumn, liveValueColumn,
                storedValueColumn);
        table.getColumns().addAll(list);

        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configureTableForDnD(table);
        return table;
    }

    private TableView<TableEntry> createTable(int numSnapshots) {
        if (numSnapshots == 1) {
            return createTableForSingleSnapshot();
        } else {
            return createTableForMultipleSnapshots(numSnapshots);
        }
    }

    /**
     * Set the base snapshot to be displayed in this editor. When the base snapshot is set, the editor is emptied
     * first. The meta data of the base snapshot are displayed at the top of the editor (comment, date, creator).
     *
     * @param data the snapshot data to set
     */
    public synchronized void setSnapshot(final VSnapshot data) {
        final List<TableEntry> entries = model.setSnapshot(data);
        final int num = model.getNumberOfSnapshots();
        Platform.runLater(() -> {
            data.getSnapshot().ifPresent(t -> {
                commentField.setText(t.getComment());
                creatorField.setText(t.getOwner());
                dateField.setText(Utilities.timestampToBigEndianString(t.getDate()));
            });
            table = createTable(num);
            contentPane.setCenter(table);
            table.getItems().setAll(entries);
        });
    }

    /**
     * Adds a snapshot to this editor. This snapshot is compared to the base snapshot.
     *
     * @param data the snapshot data
     */
    public synchronized void addSnapshot(VSnapshot data) {
        final List<TableEntry> entries = model.addSnapshot(data);
        final int num = model.getNumberOfSnapshots();
        Platform.runLater(() -> {
            table = createTable(num);
            contentPane.setCenter(table);
            table.getItems().setAll(entries);
        });
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#setFxFocus()
     */
    @Override
    public void setFxFocus() {
        if (table != null) {
            table.requestFocus();
        }
    }
}
