package org.csstudio.saverestore.ui;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.Utilities.VTypeComparison;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.MultitypeTableCell;
import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.UnfocusableCheckBox;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

/**
 *
 * <code>Table</code> is an extension of the JavaFX table, tailored for the presentation of the {@link VSnapshot}s. The
 * table also implements {@link ISelectionProvider} interface and provides the selected items as {@link ProcessVariable}
 * s or {@link TimestampedPV}s. This table is used in combination with the {@link SnapshotViewerEditor}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
class Table extends TableView<TableEntry> implements ISelectionProvider {

    private static boolean resizePolicyNotInitialized = true;
    private static PrivilegedAction<Object> resizePolicyAction = () -> {
        try {
            // Java FX bugfix: the table columns are not properly resized for the first table
            Field f = TableView.CONSTRAINED_RESIZE_POLICY.getClass().getDeclaredField("isFirstRun");
            f.setAccessible(true);
            f.set(TableView.CONSTRAINED_RESIZE_POLICY, Boolean.FALSE);
        } catch (NoSuchFieldException | IllegalAccessException | RuntimeException e) {
            // ignore
        }
        // Even if failed to set the policy, pretend that it was set. In such case the UI will be slightly dorked the
        // first time, but will be OK in all other cases.
        resizePolicyNotInitialized = false;
        return null;
    };

    /**
     * <code>TimestampTableCell</code> is a table cell for rendering the {@link Timestamp} objects in the table.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static class TimestampTableCell extends TableCell<TableEntry, Timestamp> {
        @Override
        protected void updateItem(Timestamp item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                setStyle("");
            } else {
                setText(Utilities.timestampToString(item));
            }
        }
    }

    /**
     * <code>VTypeCellEditor</code> is an editor type for {@link VType} or {@link VTypePair}, which allows editing the
     * value as a string.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     * @param <T> {@link VType} or {@link VTypePair}
     */
    private static class VTypeCellEditor<T> extends MultitypeTableCell<TableEntry, T> {
        private static final Image WARNING_IMAGE = new Image(
            SnapshotViewerEditor.class.getResourceAsStream("/icons/hprio_tsk.png"));
        private final SnapshotViewerController controller;
        private final Tooltip tooltip = new Tooltip();

        VTypeCellEditor(SnapshotViewerController cntrl) {
            this.controller = cntrl;
            setConverter(new StringConverter<T>() {
                @Override
                public String toString(T item) {
                    if (item == null) {
                        return "";
                    } else if (item instanceof VType) {
                        return Utilities.valueToString((VType) item);
                    } else if (item instanceof VTypePair) {
                        return Utilities.valueToString(((VTypePair) item).value);
                    } else {
                        return item.toString();
                    }
                }

                @SuppressWarnings("unchecked")
                @Override
                public T fromString(String string) {
                    T item = getItem();
                    try {
                        if (string == null) {
                            return item;
                        } else if (item instanceof VType) {
                            return (T) Utilities.valueFromString(string, (VType) item);
                        } else if (item instanceof VTypePair) {
                            VTypePair t = (VTypePair) item;
                            if (t.value instanceof VNoData) {
                                return (T) new VTypePair(t.base, Utilities.valueFromString(string, t.base),
                                    t.threshold);
                            } else {
                                return (T) new VTypePair(t.base, Utilities.valueFromString(string, t.value),
                                    t.threshold);
                            }
                        } else {
                            return item;
                        }
                    } catch (IllegalArgumentException e) {
                        FXMessageDialog.openError(controller.getOwner().getSite().getShell(), "Editing Error",
                            e.getMessage());
                        return item;
                    }
                }
            });
            // FX does not provide any facilities to get the column index at mouse position, so use this hack, to know
            // where the mouse is located
            setOnMouseEntered(e -> ((Table) getTableView()).setColumnAndRowAtMouse(getTableColumn(), getIndex()));
            setOnMouseExited(e -> ((Table) getTableView()).setColumnAndRowAtMouse(null, -1));
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isTextFieldType() {
            T item = getItem();
            if (item instanceof VEnum) {
                if (getItems().isEmpty()) {
                    VEnum value = (VEnum) item;
                    List<String> labels = value.getLabels();
                    List<T> values = new ArrayList<>(labels.size());
                    for (int i = 0; i < labels.size(); i++) {
                        values.add((T) ValueFactory.newVEnum(i, labels, value, value));
                    }
                    setItems(values);
                }
                return false;
            } else if (item instanceof VTypePair) {
                VTypePair v = ((VTypePair) item);
                VType type = v.value;
                if (type instanceof VEnum) {
                    if (getItems().isEmpty()) {
                        VEnum value = (VEnum) type;
                        List<String> labels = value.getLabels();
                        List<T> values = new ArrayList<>(labels.size());
                        for (int i = 0; i < labels.size(); i++) {
                            values.add(
                                (T) new VTypePair(v.base, ValueFactory.newVEnum(i, labels, value, value), v.threshold));
                        }
                        setItems(values);
                    }
                    return false;
                }
            }
            return true;
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            updateItem(getItem(), isEmpty());
        }

        /*
         * (non-Javadoc)
         *
         * @see javafx.scene.control.cell.TextFieldTableCell#updateItem(java.lang.Object, boolean)
         */
        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().remove("diff-cell");
            if (item == null || empty) {
                setText("");
                setTooltip(null);
                setGraphic(null);
            } else {
                if (item instanceof VNoData) {
                    setText(item.toString());
                    setGraphic(null);
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                } else if (item instanceof VType) {
                    setText(Utilities.valueToString((VType) item));
                    setGraphic(null);
                    tooltip.setText(item.toString());
                    setTooltip(tooltip);
                } else if (item instanceof VTypePair) {
                    VTypeComparison vtc = Utilities.valueToCompareString(((VTypePair) item).value,
                        ((VTypePair) item).base, ((VTypePair) item).threshold);
                    setText(vtc.getString());
                    if (!vtc.isWithinThreshold()) {
                        getStyleClass().add("diff-cell");
                        setGraphic(new ImageView(WARNING_IMAGE));
                    }
                    tooltip.setText(item.toString());
                    setTooltip(tooltip);
                }
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
    private class TooltipTableColumn<T> extends TableColumn<TableEntry, T> {
        private String text;
        private Label label;

        TooltipTableColumn(String text, String tooltip, int minWidth) {
            setup(text, tooltip, minWidth, -1, true);
        }

        TooltipTableColumn(String text, String tooltip, int minWidth, int prefWidth, boolean resizable) {
            setup(text, tooltip, minWidth, prefWidth, resizable);
        }

        private void setup(String text, String tooltip, int minWidth, int prefWidth, boolean resizable) {
            label = new Label(text);
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
            setOnEditStart(e -> controller.suspend());
            setOnEditCancel(e -> controller.resume());
            setOnEditCommit(e -> controller.resume());
            this.text = text;
        }

        void setSaved(boolean saved) {
            if (saved) {
                label.setText(text);
            } else {
                label.setText("*" + text + "*");
            }
        }
    }

    private final List<VSnapshot> uiSnapshots = new ArrayList<>();
    private boolean showStoredReadbacks;
    private final SnapshotViewerController controller;
    private CheckBox selectAllCheckBox;

    private TableColumn<TableEntry, ?> columnAtMouse;
    private int rowAtMouse = -1;
    private int clickedColumn = -1;
    private int clickedRow = -1;
    private final List<ISelectionChangedListener> selectionChangedListener = new CopyOnWriteArrayList<>();

    /**
     * Constructs a new table.
     *
     * @param controller the controller
     */
    Table(SnapshotViewerController controller) {
        if (resizePolicyNotInitialized) {
            AccessController.doPrivileged(resizePolicyAction);
        }
        this.controller = controller;
        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setMaxWidth(Double.MAX_VALUE);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getStylesheets().add(Table.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());

        setOnMouseClicked(e -> {
            if (getSelectionModel().getSelectedCells() != null && !getSelectionModel().getSelectedCells().isEmpty()) {
                if (columnAtMouse == null) {
                    clickedColumn = getSelectionModel().getSelectedCells().get(0).getColumn();
                } else {
                    int idx = getColumns().indexOf(columnAtMouse);
                    if (uiSnapshots.size() > 1) {
                        if (idx < 0) {
                            // it is one of the grouped stored values columns
                            idx = getColumns().get(3).getColumns().indexOf(columnAtMouse);
                            if (idx >= 0) {
                                idx += 3;
                            }
                        } else {
                            // it is either one of the first 3 columns (do nothing) or one of the live columns
                            if (idx > 3) {
                                idx = getColumns().get(3).getColumns().size() + idx - 1;
                            }
                        }
                    }
                    if (idx < 0) {
                        clickedColumn = getSelectionModel().getSelectedCells().get(0).getColumn();
                    } else {
                        clickedColumn = idx;
                    }
                }
                clickedRow = rowAtMouse == -1 ? getSelectionModel().getSelectedCells().get(0).getRow() : rowAtMouse;
                final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
                selectionChangedListener.forEach(l -> l.selectionChanged(event));
            }
        });
    }

    /**
     * Set the column and row number at current mouse position.
     *
     * @param column the column at mouse cursor (null if none)
     * @param row the row index at mouse cursor
     */
    private void setColumnAndRowAtMouse(TableColumn<TableEntry, ?> column, int row) {
        this.columnAtMouse = column;
        this.rowAtMouse = row;
    }

    private void createTableForSingleSnapshot(boolean showReadback, boolean showStoredReadback) {
        TableColumn<TableEntry, Boolean> selectedColumn = new TooltipTableColumn<>("",
            "Include this PV when restoring values", 30, 30, false);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setEditable(true);
        selectedColumn.setSortable(false);
        selectAllCheckBox = new UnfocusableCheckBox();
        selectAllCheckBox.setSelected(false);
        selectAllCheckBox
            .setOnAction(e -> getItems().forEach(te -> te.selectedProperty().setValue(selectAllCheckBox.isSelected())));
        selectedColumn.setGraphic(selectAllCheckBox);

        int width = FXUtilities.measureStringWidth("0000", Font.font(20));
        TableColumn<TableEntry, Integer> idColumn = new TooltipTableColumn<>("#",
            "The order number of the PV in the beamline set", width, width, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<TableEntry, String> pvNameColumn = new TooltipTableColumn<>("PV", "The name of the PV", 170);
        pvNameColumn.setCellValueFactory(new PropertyValueFactory<>("pvName"));

        width = FXUtilities.measureStringWidth("MM:MM:MM.MMM MMM MM", null);
        TableColumn<TableEntry, Timestamp> timestampColumn = new TooltipTableColumn<>("Timestamp",
            "Timestamp of the value when the snapshot was taken", width, width, true);
        timestampColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, Timestamp>("timestamp"));
        timestampColumn.setCellFactory(c -> new TimestampTableCell());
        timestampColumn.setPrefWidth(width);

        TableColumn<TableEntry, String> statusColumn = new TooltipTableColumn<>("Status",
            "Alarm status of the PV when the snapshot was taken", 100, 100, true);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<TableEntry, AlarmSeverity> severityColumn = new TooltipTableColumn<>("Severity",
            "Alarm severity of the PV when the snapshot was taken", 80, 80, false);
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));

        TableColumn<TableEntry, VTypePair> storedValueColumn = new TooltipTableColumn<>(
            "Stored Value (" + Utilities.DELTA_CHAR + " Setpoint)", "PV value when the snapshot was taken", 100);
        storedValueColumn.setCellValueFactory(e -> e.getValue().valueProperty());
        storedValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        storedValueColumn.setEditable(true);
        storedValueColumn.setOnEditCommit(e -> {
            ObjectProperty<VTypePair> value = e.getRowValue().valueProperty();
            value.setValue(new VTypePair(value.get().base, e.getNewValue().value, value.get().threshold));
            controller.updateSnapshot(0, e.getRowValue());
            controller.resume();
        });

        TableColumn<TableEntry, VType> liveValueColumn = new TooltipTableColumn<>("Live Setpoint", "Current PV value",
            100);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        liveValueColumn.setEditable(false);

        List<TableColumn<TableEntry, ?>> list = new ArrayList<>(Arrays.asList(selectedColumn, idColumn, pvNameColumn,
            timestampColumn, statusColumn, severityColumn, storedValueColumn));
        if (showStoredReadback) {
            TableColumn<TableEntry, VType> storedReadbackColumn = new TooltipTableColumn<>(
                "Readback (" + Utilities.DELTA_CHAR + " Setpoint)", "Stored Readback value", 100);
            storedReadbackColumn.setCellValueFactory(new PropertyValueFactory<>("storedReadback"));
            storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            storedReadbackColumn.setEditable(false);
            list.add(storedReadbackColumn);
        }
        list.add(liveValueColumn);
        if (showReadback) {
            TableColumn<TableEntry, VType> readbackColumn = new TooltipTableColumn<>(
                "Live Readback (" + Utilities.DELTA_CHAR + " Setpoint)", "Current Readback value", 100);
            readbackColumn.setCellValueFactory(new PropertyValueFactory<>("readback"));
            readbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            readbackColumn.setEditable(false);
            list.add(readbackColumn);
        }
        getColumns().addAll(list);
    }

    private void createTableForMultipleSnapshots(List<VSnapshot> snapshots, boolean showReadback,
        boolean showStoredReadback) {
        TableColumn<TableEntry, Boolean> selectedColumn = new TooltipTableColumn<>("",
            "Include this PV when restoring values", 30, 30, false);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setEditable(true);
        selectedColumn.setSortable(false);
        selectAllCheckBox = new UnfocusableCheckBox();
        selectAllCheckBox.setSelected(false);
        selectAllCheckBox
            .setOnAction(e -> getItems().forEach(te -> te.selectedProperty().setValue(selectAllCheckBox.isSelected())));
        selectedColumn.setGraphic(selectAllCheckBox);

        int width = FXUtilities.measureStringWidth("0000", Font.font(20));
        TableColumn<TableEntry, Integer> idColumn = new TooltipTableColumn<>("#",
            "The order number of the PV in the beamline set", width, width, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TableEntry, String> pvNameColumn = new TooltipTableColumn<>("PV", "The name of the PV", 170);
        pvNameColumn.setCellValueFactory(new PropertyValueFactory<>("pvName"));

        TableColumn<TableEntry, VType> liveValueColumn = new TooltipTableColumn<>("Live Setpoint", "Current PV value",
            100);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        liveValueColumn.setEditable(false);

        TableColumn<TableEntry, ?> storedValueColumn = new TooltipTableColumn<>("Stored Values",
            "PV value when the snapshot was taken", -1);
        TableColumn<TableEntry, VTypePair> baseCol = new TooltipTableColumn<>(
            "Base (" + Utilities.DELTA_CHAR + " Setpoint)", "PV value when the snapshot was taken", 100);
        baseCol.setCellValueFactory(e -> e.getValue().valueProperty());
        baseCol.setCellFactory(e -> new VTypeCellEditor<>(controller));
        baseCol.setEditable(true);
        baseCol.setOnEditCommit(e -> {
            ObjectProperty<VTypePair> value = e.getRowValue().valueProperty();
            value.setValue(new VTypePair(value.get().base, e.getNewValue().value, value.get().threshold));
            controller.updateSnapshot(0, e.getRowValue());
            controller.resume();
        });
        storedValueColumn.getColumns().add(baseCol);
        if (showStoredReadback) {
            TableColumn<TableEntry, VTypePair> storedReadbackColumn = new TooltipTableColumn<>(
                "Readback (" + Utilities.DELTA_CHAR + " Setpoint)", "Stored Readback value", 100);
            storedReadbackColumn.setCellValueFactory(e -> e.getValue().storedReadbackProperty());
            storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            storedReadbackColumn.setEditable(false);
            storedValueColumn.getColumns().add(storedReadbackColumn);
        }
        for (int i = 1; i < snapshots.size(); i++) {
            TooltipTableColumn<VTypePair> col = new TooltipTableColumn<>(
                snapshots.get(i).toString() + " (" + Utilities.DELTA_CHAR + " Base)",
                "PV value when the snapshot was taken", 100);
            final int snapshotIndex = i;
            MenuItem removeItem = new MenuItem("Remove");
            removeItem.setOnAction(ev -> SaveRestoreService.getInstance().execute("Remove Snapshot",
                () -> update(controller.removeSnapshot(snapshotIndex))));
            MenuItem setAsBaseItem = new MenuItem("Set As Base");
            setAsBaseItem.setOnAction(ev -> SaveRestoreService.getInstance().execute("Set new base Snapshot",
                () -> update(controller.setAsBase(snapshotIndex))));
            MenuItem moveToNewEditor = new MenuItem("Move To New Editor");
            moveToNewEditor.setOnAction(ev -> SaveRestoreService.getInstance().execute("Open Snapshot",
                () -> update(controller.moveSnapshotToNewEditor(snapshotIndex))));

            final ContextMenu menu = new ContextMenu(removeItem, setAsBaseItem, new SeparatorMenuItem(),
                moveToNewEditor);
            col.label.setContextMenu(menu);
            col.setCellValueFactory(e -> e.getValue().compareValueProperty(snapshotIndex));
            col.setCellFactory(e -> new VTypeCellEditor<>(controller));
            col.setEditable(true);
            col.setOnEditCommit(e -> {
                ObjectProperty<VTypePair> value = e.getRowValue().compareValueProperty(snapshotIndex);
                value.setValue(new VTypePair(value.get().base, e.getNewValue().value, value.get().threshold));
                controller.updateSnapshot(snapshotIndex, e.getRowValue());
                controller.resume();
            });
            col.label.setOnMouseReleased(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    menu.show(col.label, e.getScreenX(), e.getScreenY());
                }
            });
            storedValueColumn.getColumns().add(col);
            if (showStoredReadback) {
                TableColumn<TableEntry, VTypePair> storedReadbackColumn = new TooltipTableColumn<>(
                    "Readback (" + Utilities.DELTA_CHAR + " Setpoint)", "Stored Readback value", 100);
                storedReadbackColumn
                    .setCellValueFactory(e -> e.getValue().compareStoredReadbackProperty(snapshotIndex));
                storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
                storedReadbackColumn.setEditable(false);
                storedValueColumn.getColumns().add(storedReadbackColumn);
            }
        }
        if (showReadback) {
            TableColumn<TableEntry, VType> readbackColumn = new TooltipTableColumn<>(
                "Live Readback (" + Utilities.DELTA_CHAR + " Live Setpoint)", "Current Readback value", 100);
            readbackColumn.setCellValueFactory(new PropertyValueFactory<>("readback"));
            readbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            readbackColumn.setEditable(false);
            getColumns().addAll(Arrays.asList(selectedColumn, idColumn, pvNameColumn, storedValueColumn,
                liveValueColumn, readbackColumn));
        } else {
            getColumns()
                .addAll(Arrays.asList(selectedColumn, idColumn, pvNameColumn, storedValueColumn, liveValueColumn));
        }
    }

    private void update(final List<TableEntry> entries) {
        final List<VSnapshot> snaps = controller.getAllSnapshots();
        // the readback properties are changed on the UI thread, however they are just flags, which do not have any
        // effect on the data model, so they can be read by anyone at anytime
        Platform.runLater(
            () -> updateTable(entries, snaps, controller.isShowReadbacks(), controller.isShowStoredReadbacks()));
    }

    /**
     * Updates the table by setting new content, including the structure. The table is always recreated, even if the new
     * structure is identical to the old one. This is slightly more expensive; however, this method is only invoked per
     * user request (button click).
     *
     * @param entries the table entries (rows) to set on the table
     * @param snapshots the snapshots which are currently displayed
     * @param showReadback true if readback column should be visible or false otherwise
     * @param showStoredReadback true if the stored readback value columns should be visible or false otherwise
     */
    void updateTable(List<TableEntry> entries, List<VSnapshot> snapshots, boolean showReadback,
        boolean showStoredReadback) {
        getColumns().clear();
        uiSnapshots.clear();
        // we should always know if we are showing the stored readback or not, to properly extract the selection
        this.showStoredReadbacks = showStoredReadback;
        uiSnapshots.addAll(snapshots);
        if (uiSnapshots.size() == 1) {
            createTableForSingleSnapshot(showReadback, showStoredReadback);
        } else {
            createTableForMultipleSnapshots(snapshots, showReadback, showStoredReadback);
        }
        updateTableColumnTitles();
        updateTable(entries);
    }

    /**
     * Sets new table entries for this table, but do not change the structure of the table.
     *
     * @param entries the entries to set
     */
    void updateTable(List<TableEntry> entries) {
        final ObservableList<TableEntry> items = getItems();
        final boolean notHide = !controller.isHideEqualItems();
        items.clear();
        entries.forEach(e -> {
            // there is no harm if this is executed more than once, because only one listener is allowed for these
            // two properties (see SingleListenerBooleanProperty for more details)
            e.selectedProperty()
                .addListener((a, o, n) -> selectAllCheckBox.setSelected(n ? selectAllCheckBox.isSelected() : false));
            e.liveStoredEqualProperty().addListener((a, o, n) -> {
                if (controller.isHideEqualItems()) {
                    if (n) {
                        getItems().remove(e);
                    } else {
                        getItems().add(e);
                    }
                }
            });
            if (notHide || !e.liveStoredEqualProperty().get()) {
                items.add(e);
            }
        });
    }

    /**
     * Add a new item to the table.
     *
     * @param entry the item to add
     */
    void addItem(TableEntry entry) {
        entry.selectedProperty()
            .addListener((a, o, n) -> selectAllCheckBox.setSelected(n ? selectAllCheckBox.isSelected() : false));
        entry.liveStoredEqualProperty().addListener((a, o, n) -> {
            if (controller.isHideEqualItems()) {
                if (n) {
                    getItems().remove(entry);
                } else {
                    getItems().add(entry);
                }
            }
        });
        if (!controller.isHideEqualItems() || !entry.liveStoredEqualProperty().get()) {
            getItems().add(entry);
        }
    }

    /**
     * Update the table column titles, by putting an asterisk to non saved snapshots or remove asterisk from saved
     * snapshots.
     */
    void updateTableColumnTitles() {
        // add the * to the title of the column if the snapshot is not saved
        if (uiSnapshots.size() == 1) {
            ((TooltipTableColumn<?>) getColumns().get(6)).setSaved(uiSnapshots.get(0).isSaved());
        } else {
            TableColumn<TableEntry, ?> column = (TableColumn<TableEntry, ?>) getColumns().get(3);
            for (int i = 0; i < uiSnapshots.size(); i++) {
                ((TooltipTableColumn<?>) column.getColumns().get(i)).setSaved(uiSnapshots.get(i).isSaved());
            }
        }
    }

    /**
     * Replace the old snapshot reference with the new one. This only replaces the entry in the list of snapshots in
     * order for the column titles to be properly updated.
     *
     * @param old the old value already present in the table
     * @param n the new value which should be identical to the old value, except that it is saved
     * @return always true (for pragmatic reasons)
     */
    boolean replaceSnapshot(VSnapshot old, VSnapshot n) {
        Platform.runLater(() -> {
            int idx = uiSnapshots.indexOf(old);
            if (idx > -1) {
                uiSnapshots.set(idx, n);
                updateTableColumnTitles();
            }
        });
        return true;
    }

    /**
     * Returns the snapshot that is currently selected in the UI. Selected snapshot is the snapshot which was last
     * clicked.
     *
     * @return the selected snapshot
     */
    VSnapshot getSelectedSnapshot() {
        int numSnapshots = uiSnapshots.size();
        if (numSnapshots == 0) {
            return null;
        }
        // find the snapshot that matches the clicked column
        VSnapshot snapshot;
        if (numSnapshots == 1 || clickedColumn < 4) {
            snapshot = uiSnapshots.get(0);
        } else {
            int subColumns = getColumns().get(3).getColumns().size();
            if (2 + subColumns < clickedColumn) {
                // clicked on one of the live columns - in this case select the right most snapshot
                snapshot = uiSnapshots.get(numSnapshots - 1);
            } else {
                int clickedSubColumn = clickedColumn - 3;
                if (showStoredReadbacks) {
                    snapshot = uiSnapshots.get(clickedSubColumn / 2);
                } else {
                    snapshot = uiSnapshots.get(clickedSubColumn);
                }
            }
        }
        return snapshot;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    @Override
    public ISelection getSelection() {
        VSnapshot snapshot = getSelectedSnapshot();
        if (snapshot == null) {
            return null;
        }
        // if snapshot was found, use its timestamp and create timestamped PVs
        Timestamp timestamp = snapshot.getTimestamp();
        if (timestamp == null) {
            return new StructuredSelection(selectionModelProperty().get().getSelectedItems().stream()
                .map(e -> new ProcessVariable(e.pvNameProperty().get())).collect(Collectors.toList()));
        } else {
            long time = timestamp.toDate().getTime();
            return new StructuredSelection(selectionModelProperty().get().getSelectedItems().stream()
                .map(e -> new TimestampedPV(e.pvNameProperty().get(), time)).collect(Collectors.toList()));
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
        selectionChangedListener.add(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListener.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setSelection(ISelection selection) {
        // select action not supported
    }

    /**
     * Returns the most recently clicked (selected) item in the table. If the clicked column is a value column, that
     * value is returned. If the clicked column is not a value column, the stored value of the base snapshot is
     * returned.
     *
     * @return the item clicked in the table
     */
    VTypeNamePair getClickedItem() {
        if (clickedRow < 0) {
            return null;
        }
        int numSnapshots = uiSnapshots.size();
        if (numSnapshots == 0) {
            return null;
        } else if (numSnapshots == 1) {
            // in case of a single snapshot it is easy, because the colums are single
            Object obj = getColumns().get(clickedColumn).getCellData(clickedRow);
            return extractValue(obj, clickedRow);
        } else {
            if (clickedColumn < 4) {
                Object obj = getColumns().get(3).getColumns().get(0).getCellData(clickedRow);
                return extractValue(obj, clickedRow);
            } else {
                int subColumns = getColumns().get(3).getColumns().size();
                if (clickedColumn < subColumns + 3) {
                    Object obj = getColumns().get(3).getColumns().get(clickedColumn - 3).getCellData(clickedRow);
                    return extractValue(obj, clickedRow);
                } else {
                    Object obj = getColumns().get(clickedColumn - subColumns + 1).getCellData(clickedRow);
                    return extractValue(obj, clickedRow);
                }
            }
        }
    }

    /**
     * Extract the VType data from the cellData object (if possible) and return the pair containing that value and the
     * pv name at the given row.
     *
     * @param cellData the cell data object (should belong to the row given as a parameter)
     * @param row the row to extract the pv name
     * @return the value name pair if the cell data is value or null if cell data is anything else.
     */
    private VTypeNamePair extractValue(Object cellData, int row) {
        VType value;
        if (cellData instanceof VType) {
            value = (VType) cellData;
        } else if (cellData instanceof VTypePair) {
            value = ((VTypePair) cellData).value;
        } else {
            return null;
        }
        TableEntry entry = getItems().get(clickedRow);
        return new VTypeNamePair(value, entry.pvNameProperty().get());
    }
}
