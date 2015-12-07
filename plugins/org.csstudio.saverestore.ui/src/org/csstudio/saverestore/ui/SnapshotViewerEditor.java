package org.csstudio.saverestore.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.SnapshotDataFormat;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.csstudio.ui.fx.util.FXComboInputDialog;
import org.csstudio.ui.fx.util.FXEditorPart;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 *
 * <code>SnapshotViewerEditor</code> is an {@link IEditorPart} implementation for displaying, creating, comparing, and
 * restoring snapshots. It provides a table that displays the stored values (together with the alarm data and timestamp)
 * and live data. In comparison view only the values are displayed. At the top of the editor buttons for taking, saving
 * and restoring the snapshot are located.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotViewerEditor extends FXEditorPart implements ISelectionProvider {

    public static final String ID = "org.csstudio.saverestore.ui.editor.snapshotviewer";
    private static boolean resizePolicyNotInitialized = true;

    /**
     *
     * <code>VTypeCellEditor</code> is an editor type for {@link VType} or {@link VTypePair}, which allows editing the
     * value as a string.
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
         *
         * @see javafx.scene.control.cell.TextFieldTableCell#updateItem(java.lang.Object, boolean)
         */
        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText("---");
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
     * <code>TooltipTableColumn</code> is the common table column implementation, which can also provide the tooltip.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     * @param <T> the type of the values displayed by this column
     */
    private static class TooltipTableColumn<T> extends TableColumn<TableEntry, T> {
        TooltipTableColumn(String text, String tooltip, int minWidth) {
            setup(text, tooltip, minWidth, -1, true);
        }

        TooltipTableColumn(String text, String tooltip, int minWidth, int prefWidth, boolean resizable) {
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

    // the style for the animated save button
    private static final String ANIMATED_STYLE = "-fx-background-color: #FF8080; -fx-text-fill: white; "
            + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1);";

    private Scene scene;
    private BorderPane contentPane;
    private TableView<TableEntry> table;
    private SnapshotViewerController controller;
    private TextArea commentField;
    private TextField dateField;
    private TextField creatorField;
    private Button takeSnapshotButton;
    private Button restoreSnapshotButton;
    private Button saveSnapshotButton;
    private CheckBox selectAllCheckBox;
    private FadeTransition animation;

    private int clickedColumn = -1;

    /**
     * Constructs a new editor.
     */
    public SnapshotViewerEditor() {
        controller = new SnapshotViewerController(this);
    }

    private Menu contextMenu;

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        MenuManager menu = new MenuManager();
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        contextMenu = menu.createContextMenu(parent);
        parent.setMenu(contextMenu);
        getSite().registerContextMenu(menu, this);
    }

    private void init() {
        if (resizePolicyNotInitialized) {
            try {
                Field f = TableView.CONSTRAINED_RESIZE_POLICY.getClass().getDeclaredField("isFirstRun");
                f.setAccessible(true);
                f.set(TableView.CONSTRAINED_RESIZE_POLICY, Boolean.FALSE);
            } catch (Exception e) {
                // ignore
            }
            resizePolicyNotInitialized = false;
        }

        animation = new FadeTransition(Duration.seconds(0.15), saveSnapshotButton);
        animation.setAutoReverse(true);
        animation.setFromValue(1.0);
        animation.setToValue(0.4);
        animation.setCycleCount(6);
        controller.snapshotSaveableProperty().addListener((a, o, n) -> {
            if (!n) {
                animation.pause();
                animation.jumpTo(Duration.seconds(0));
                animation.stop();
                saveSnapshotButton.setStyle(null);
            } else if (animation.getStatus() != Status.RUNNING) {
                saveSnapshotButton.setStyle(ANIMATED_STYLE);
                animation.play();
            }
            firePropertyChange(PROP_DIRTY);
        });
        if (controller.snapshotSaveableProperty().get()) {
            saveSnapshotButton.setStyle(ANIMATED_STYLE);
            animation.play();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        if (getEditorInput() instanceof IFileEditorInput) {
            save(monitor, false);
        } else {
            SaveRestoreService.getInstance().execute("Save Snapshot", () -> save(monitor, false));
        }
    }

    private void save(final IProgressMonitor monitor, boolean saveAs) {
        List<VSnapshot> snapshots = new ArrayList<>();
        snapshots.addAll(controller.getSnapshots(true));
        do {
            if (!save(snapshots, saveAs).map(snapshots::remove).isPresent()) {
                break;
            }
        } while (!snapshots.isEmpty());
        getSite().getShell().getDisplay().asyncExec(() -> {
            monitor.done();
            firePropertyChange(PROP_DIRTY);
        });
    }

    private void doSave(final List<VSnapshot> snapshots) {
        if (getEditorInput() instanceof IFileEditorInput) {
            // saving to a resource should always happen in the UI thread, because of the RCP locking system
            save(snapshots, false);
        } else {
            SaveRestoreService.getInstance().execute("Save Snapshot", () -> save(snapshots, false));
        }
    }

    private Optional<VSnapshot> save(List<VSnapshot> snapshots, final boolean saveAs) {
        if (snapshots.isEmpty()) {
            return null;
        }
        if (getEditorInput() instanceof IFileEditorInput) {
            if (snapshots.size() == 1) {
                return Optional.ofNullable(controller.saveSnapshotToFile(snapshots.remove(0),
                        (IFileEditorInput) getEditorInput(), saveAs));
            } else {
                return FXComboInputDialog
                        .pick(getSite().getShell(), "Select Snapshot", "Select the snapshot that you wish to save",
                                snapshots.get(0), snapshots)
                        .map(e -> controller.saveSnapshotToFile(e, (IFileEditorInput) getEditorInput(), saveAs));
            }
        } else {
            if (snapshots.size() == 1) {
                return Optional.ofNullable(controller.saveSnapshot(snapshots.remove(0)));
            } else {
                return FXComboInputDialog.pick(getSite().getShell(), "Select Snapshot",
                        "Select the snapshot that you wish to save", snapshots.get(0), snapshots)
                        .map(controller::saveSnapshot);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        if (getEditorInput() instanceof IFileEditorInput) {
            save(new NullProgressMonitor(), true);
        } else {
            SaveRestoreService.getInstance().execute("Save Snapshot", () -> doSave(new NullProgressMonitor()));
        }
    }

    /*
     * (non-Javadoc)
     *
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
     *
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        controller.dispose();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return controller.snapshotSaveableProperty().get();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return getEditorInput() instanceof IFileEditorInput;
    }

    /*
     * (non-Javadoc)
     *
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
        topPane.add(top, 0, 0);
        topPane.add(bottom, 1, 0);

        contentPane.setTop(topPane);
        scene = new Scene(contentPane);
        init();
        return scene;
    }

    private Node createTopPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.TOP_LEFT);
        commentField = new TextArea() {
            @Override
            public void requestFocus() {
            }
        };
        commentField.setEditable(false);
        commentField.setWrapText(true);
        commentField.setPrefWidth(300);
        commentField.setPrefRowCount(2);
        GridPane.setVgrow(commentField, Priority.ALWAYS);
        GridPane.setFillHeight(commentField, true);
        creatorField = new TextField() {
            @Override
            public void requestFocus() {
            }
        };
        creatorField.setEditable(false);
        creatorField.setPrefWidth(150);
        dateField = new TextField() {
            @Override
            public void requestFocus() {
            }
        };
        dateField.setEditable(false);
        dateField.setPrefWidth(150);

        grid.add(new Label("Comment:"), 0, 0);
        grid.add(commentField, 1, 0, 1, 2);
        grid.add(new Label("Creator:"), 2, 0);
        grid.add(new Label("Timestamp:"), 2, 1);
        grid.add(creatorField, 3, 0);
        grid.add(dateField, 3, 1);
        return grid;
    }

    private Node createButtonPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.setHgap(5);

        takeSnapshotButton = new Button();
        VBox box = new VBox();
        box.getChildren().addAll(new Label("Take"), new Label("Snapshot"));
        box.setAlignment(Pos.CENTER);
        takeSnapshotButton.setGraphic(box);
        takeSnapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        takeSnapshotButton.setTooltip(new Tooltip("Take a new snapshot (locally store the live values)"));
        takeSnapshotButton.setOnAction(
                e -> SaveRestoreService.getInstance().execute("Take Snapshot", () -> controller.takeSnapshot()));

        saveSnapshotButton = new Button();
        box = new VBox();
        box.getChildren().addAll(new Label("Save"), new Label("Snapshot"));
        box.setAlignment(Pos.CENTER);
        saveSnapshotButton.setGraphic(box);
        saveSnapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        saveSnapshotButton.setTooltip(new Tooltip("Save the snapshot values as a new revision"));

        saveSnapshotButton.setOnAction(e -> doSave(new ArrayList<>(controller.getSnapshots(true))));

        saveSnapshotButton.disableProperty().bind(controller.snapshotSaveableProperty().not());

        restoreSnapshotButton = new Button("Restore");
        restoreSnapshotButton.setTooltip(new Tooltip("Set the stored values to PVs"));
        restoreSnapshotButton.setOnAction(e -> SaveRestoreService.getInstance().execute("Restore Snapshot", () -> {
            List<VSnapshot> snapshots = controller.getSnapshots(false);
            if (snapshots.isEmpty()) {
                return;
            } else if (snapshots.size() == 1) {
                controller.restoreSnapshot(snapshots.get(0));
            } else {
                FXComboInputDialog.pick(getSite().getShell(), "Select Snapshot",
                        "Select the snapshot that you wish to restore", snapshots.get(0), snapshots)
                        .ifPresent(controller::restoreSnapshot);
            }
        }));
        restoreSnapshotButton.disableProperty().bind(controller.snapshotRestorableProperty().not());

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
        final TableView<TableEntry> table = new TableView<>();
        TableColumn<TableEntry, Boolean> selectedColumn = new TooltipTableColumn<>("",
                "Include this PV when restoring values", 30, 30, false);

        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setEditable(true);
        selectedColumn.setSortable(false);
        selectAllCheckBox = new CheckBox();
        selectAllCheckBox.setSelected(false);
        selectAllCheckBox.setOnAction(
                e -> table.getItems().forEach(te -> te.selectedProperty().setValue(selectAllCheckBox.isSelected())));
        selectedColumn.setGraphic(selectAllCheckBox);

        TableColumn<TableEntry, Integer> idColumn = new TooltipTableColumn<>("#",
                "The order number of the PV in the beamline set", 30, 30, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TableEntry, String> pvNameColumn = new TooltipTableColumn<>("PV", "The name of the PV", 170);
        pvNameColumn.setCellValueFactory(new PropertyValueFactory<>("pvName"));

        TableColumn<TableEntry, Timestamp> timestampColumn = new TooltipTableColumn<>("Timestamp",
                "Timestamp of the value when the snapshot was taken", 120, 135, true);
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

        TableColumn<TableEntry, String> statusColumn = new TooltipTableColumn<>("Status",
                "Alarm status of the PV when the snapshot was taken", 100, 100, true);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<TableEntry, AlarmSeverity> severityColumn = new TooltipTableColumn<>("Severity",
                "Alarm severity of the PV when the snapshot was taken", 80, 80, false);
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));

        TableColumn<TableEntry, VType> storedValueColumn = new TooltipTableColumn<>("Stored Value",
                "PV value when the snapshot was taken", 100);
        storedValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        storedValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        storedValueColumn.setEditable(true);
        storedValueColumn.setOnEditCommit((e) -> {
            ((TableEntry) e.getTableView().getItems().get(e.getTablePosition().getRow())).valueProperty()
                    .setValue(e.getNewValue());
        });

        TableColumn<TableEntry, VType> liveValueColumn = new TooltipTableColumn<>("Live Value", "Current PV value",
                100);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        liveValueColumn.setEditable(false);

        List<TableColumn<TableEntry, ?>> list = Arrays.asList(selectedColumn, idColumn, pvNameColumn, timestampColumn,
                statusColumn, severityColumn, storedValueColumn, liveValueColumn);
        table.getColumns().addAll(list);

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
                    final DataProvider provider = SaveRestoreService.getInstance()
                            .getDataProvider(s.getBeamlineSet().getDataProviderId()).provider;
                    SaveRestoreService.getInstance().execute("Load snapshot data", () -> {
                        try {
                            VSnapshot vs = provider.getSnapshotContent(s);
                            addSnapshot(vs);
                        } catch (DataProviderException ex) {
                            Selector.reportException(ex, getSite().getShell());
                        }
                    });
                }
            }
        });
    }

    private TableView<TableEntry> createTableForMultipleSnapshots(int n) {
        final TableView<TableEntry> table = new TableView<>();
        TableColumn<TableEntry, Boolean> selectedColumn = new TooltipTableColumn<>("",
                "Include this PV when restoring values", 30, 30, false);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setEditable(true);
        selectedColumn.setSortable(false);
        selectAllCheckBox = new CheckBox();
        selectAllCheckBox.setSelected(false);
        selectAllCheckBox.setOnAction(
                e -> table.getItems().forEach(te -> te.selectedProperty().setValue(selectAllCheckBox.isSelected())));
        selectedColumn.setGraphic(selectAllCheckBox);

        TableColumn<TableEntry, Integer> idColumn = new TooltipTableColumn<>("#",
                "The order number of the PV in the beamline set", 30, 30, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TableEntry, String> pvNameColumn = new TooltipTableColumn<>("PV", "The name of the PV", 170);
        pvNameColumn.setCellValueFactory(new PropertyValueFactory<>("pvName"));

        TableColumn<TableEntry, VType> liveValueColumn = new TooltipTableColumn<>("Live Value", "Current PV value", -1);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        liveValueColumn.setEditable(false);

        TableColumn<TableEntry, ?> storedValueColumn = new TooltipTableColumn<>("Stored Values",
                "PV value when the snapshot was taken", -1);

        TableColumn<TableEntry, VType> baseCol = new TooltipTableColumn<>("Base",
                "PV value when the snapshot was taken", 100);
        baseCol.setCellValueFactory(e -> e.getValue().valueProperty());
        baseCol.setCellFactory(e -> new VTypeCellEditor<>());
        storedValueColumn.getColumns().add(baseCol);

        for (int i = 1; i < n; i++) {
            TableColumn<TableEntry, VTypePair> col = new TooltipTableColumn<>(controller.getSnapshot(i).toString(),
                    "PV value when the snapshot was taken", 100);
            final int a = i;
            col.setCellValueFactory(e -> e.getValue().compareValueProperty(a));
            col.setCellFactory(e -> new VTypeCellEditor<>());
            col.setEditable(true);
            storedValueColumn.getColumns().add(col);
        }

        List<TableColumn<TableEntry, ?>> list = Arrays.asList(selectedColumn, idColumn, pvNameColumn, storedValueColumn,
                liveValueColumn);
        table.getColumns().addAll(list);
        return table;
    }

    private TableView<TableEntry> createTable(int numSnapshots) {
        TableView<TableEntry> table;
        if (numSnapshots == 1) {
            table = createTableForSingleSnapshot();
        } else {
            table = createTableForMultipleSnapshots(numSnapshots);
        }
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configureTableForDnD(table);
        table.setOnMouseReleased(e -> contextMenu.setVisible(e.getButton() == MouseButton.SECONDARY));
        table.setOnMouseClicked(e -> clickedColumn = table.getSelectionModel().getSelectedCells().get(0).getColumn());
        return table;

    }

    /**
     * Set the base snapshot to be displayed in this editor. When the base snapshot is set, the editor is emptied first.
     * The meta data of the base snapshot are displayed at the top of the editor (comment, date, creator).
     *
     * @param data the snapshot data to set
     */
    public void setSnapshot(final VSnapshot data) {
        SaveRestoreService.getInstance().execute("Open Snapshot", () -> {
            final List<TableEntry> entries = controller.setSnapshot(data);
            final int num = controller.getNumberOfSnapshots();
            Platform.runLater(() -> {
                data.getSnapshot().ifPresent(t -> {
                    commentField.setText(t.getComment());
                    creatorField.setText(t.getOwner());
                    dateField.setText(Utilities.timestampToBigEndianString(t.getDate(), true));
                });
                table = createTable(num);
                entries.forEach(e -> e.selectedProperty().addListener(
                        (a, o, n) -> selectAllCheckBox.setSelected(n ? selectAllCheckBox.isSelected() : false)));
                contentPane.setCenter(table);
                table.getItems().setAll(entries);
            });
        });
    }

    /**
     * Adds a snapshot to this editor. This snapshot is compared to the base snapshot.
     *
     * @param data the snapshot data
     */
    public void addSnapshot(VSnapshot data) {
        Runnable r = () -> {
            final List<TableEntry> entries = controller.addSnapshot(data);
            final int num = controller.getNumberOfSnapshots();
            Platform.runLater(() -> {
                table = createTable(num);
                entries.forEach(e -> e.selectedProperty().addListener(
                        (a, o, n) -> selectAllCheckBox.setSelected(n ? selectAllCheckBox.isSelected() : false)));
                contentPane.setCenter(table);
                table.getItems().setAll(entries);
            });
        };

        if (Platform.isFxApplicationThread()) {
            SaveRestoreService.getInstance().execute("Add Snapshot", r);
        } else {
            r.run();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#setFxFocus()
     */
    @Override
    public void setFxFocus() {
        if (table != null) {
            table.requestFocus();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    @Override
    public ISelection getSelection() {
        if (table == null) {
            return null;
        } else {
            // TablePosition focusedCell = table.getFocusModel().getFocusedCell();
            // int col = focusedCell.getColumn();
            // System.out.println(col);
            Timestamp timestamp = null;
            if (controller.getNumberOfSnapshots() == 1 || clickedColumn < 0) {
                timestamp = controller.getSnapshot(0).getTimestamp();
            } else {
                timestamp = controller.getSnapshot(clickedColumn - 3).getTimestamp();
            }

            if (timestamp == null) {
                List<ProcessVariable> list = new ArrayList<>();
                for (TableEntry e : table.selectionModelProperty().get().getSelectedItems()) {
                    list.add(new ProcessVariable(e.pvNameProperty().get()));
                }
                return new StructuredSelection(list);
            } else {
                long time = timestamp.toDate().getTime();
                List<TimestampedPV> list = new ArrayList<>();
                for (TableEntry e : table.selectionModelProperty().get().getSelectedItems()) {
                    list.add(new TimestampedPV(e.pvNameProperty().get(), time));
                }
                return new StructuredSelection(list);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setSelection(ISelection selection) {
    }
}
