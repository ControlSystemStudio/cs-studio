package org.csstudio.saverestore.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.Utilities.VTypeComparison;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.UnfocusableCheckBox;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
public class Table extends TableView<TableEntry> implements ISelectionProvider {

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
        private static final Image WARNING_IMAGE = new Image(
            SnapshotViewerEditor.class.getResourceAsStream("/icons/hprio_tsk.png"));

        VTypeCellEditor() {
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
                    if (string == null) {
                        return null;
                    } else if (item instanceof VType) {
                        return (T) Utilities.valueFromString(string, (VType) item);
                    } else if (item instanceof VTypePair) {
                        VTypePair t = (VTypePair) item;
                        if (t.value instanceof VNoData) {
                            return (T) new VTypePair(t.base, Utilities.valueFromString(string, t.base), t.threshold);
                        } else {
                            return (T) new VTypePair(t.base, Utilities.valueFromString(string, t.value), t.threshold);
                        }
                    } else {
                        return null;
                    }
                }
            });
            setTooltip(new Tooltip());
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
                getTooltip().setText(null);
                setGraphic(null);
            } else {
                if (item instanceof VNoData) {
                    setText(item.toString());
                    setGraphic(null);
                    getTooltip().setText("No Value Available");
                } else if (item instanceof VType) {
                    setText(Utilities.valueToString((VType) item));
                    setGraphic(null);
                    getTooltip().setText(item.toString());
                } else if (item instanceof VTypePair) {
                    VTypeComparison vtc = Utilities.valueToCompareString(((VTypePair) item).value,
                        ((VTypePair) item).base, ((VTypePair) item).threshold);
                    setText(vtc.string);
                    if (!vtc.withinThreshold) {
                        getStyleClass().add("diff-cell");
                        setGraphic(new ImageView(WARNING_IMAGE));
                    }
                    getTooltip().setText(item.toString());
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

    private List<VSnapshot> uiSnapshots = new ArrayList<>();
    private final SnapshotViewerController controller;
    private CheckBox selectAllCheckBox;

    private int clickedColumn = -1;
    private List<ISelectionChangedListener> selectionChangedListener = new CopyOnWriteArrayList<>();

    /**
     * Constructs a new table.
     *
     * @param controller the controller
     */
    public Table(SnapshotViewerController controller) {
        if (resizePolicyNotInitialized) {
            try {
                // Java FX bugfix: the table columns are not properly resized for the first table
                Field f = TableView.CONSTRAINED_RESIZE_POLICY.getClass().getDeclaredField("isFirstRun");
                f.setAccessible(true);
                f.set(TableView.CONSTRAINED_RESIZE_POLICY, Boolean.FALSE);
            } catch (Exception e) {
                // ignore
            }
            resizePolicyNotInitialized = false;
        }
        this.controller = controller;
        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setMaxWidth(Double.MAX_VALUE);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getStylesheets().add(this.getClass().getResource(SnapshotViewerEditor.STYLE).toExternalForm());

        setOnMouseClicked(e -> {
            if (getSelectionModel().getSelectedCells() != null && !getSelectionModel().getSelectedCells().isEmpty()) {
                clickedColumn = getSelectionModel().getSelectedCells().get(0).getColumn();
                final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
                selectionChangedListener.forEach(l -> l.selectionChanged(event));
            }
        });
    }

    private void createTableForSingleSnapshot(VSnapshot snapshot, boolean showReadback, boolean showStoredReadback) {
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
        timestampColumn.setCellFactory(c -> new TableCell<TableEntry, Timestamp>() {
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
        });
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
        storedValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
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
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        liveValueColumn.setEditable(false);

        List<TableColumn<TableEntry, ?>> list = new ArrayList<>(Arrays.asList(selectedColumn, idColumn, pvNameColumn,
            timestampColumn, statusColumn, severityColumn, storedValueColumn));
        if (showStoredReadback) {
            TableColumn<TableEntry, VType> storedReadbackColumn = new TooltipTableColumn<>(
                "Readback (" + Utilities.DELTA_CHAR + " Setpoint)", "Stored Readback value", 100);
            storedReadbackColumn.setCellValueFactory(new PropertyValueFactory<>("storedReadback"));
            storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>());
            storedReadbackColumn.setEditable(false);
            list.add(storedReadbackColumn);
        }
        list.add(liveValueColumn);
        if (showReadback) {
            TableColumn<TableEntry, VType> readbackColumn = new TooltipTableColumn<>(
                "Live Readback (" + Utilities.DELTA_CHAR + " Setpoint)", "Current Readback value", 100);
            readbackColumn.setCellValueFactory(new PropertyValueFactory<>("readback"));
            readbackColumn.setCellFactory(e -> new VTypeCellEditor<>());
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
            -1);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>());
        liveValueColumn.setEditable(false);

        TableColumn<TableEntry, ?> storedValueColumn = new TooltipTableColumn<>("Stored Values",
            "PV value when the snapshot was taken", -1);
        TableColumn<TableEntry, VTypePair> baseCol = new TooltipTableColumn<>(
            "Base (" + Utilities.DELTA_CHAR + " Setpoint)", "PV value when the snapshot was taken", 100);
        baseCol.setCellValueFactory(e -> e.getValue().valueProperty());
        baseCol.setCellFactory(e -> new VTypeCellEditor<>());
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
            storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>());
            storedReadbackColumn.setEditable(false);
            storedValueColumn.getColumns().add(storedReadbackColumn);
        }
        for (int i = 1; i < snapshots.size(); i++) {
            TooltipTableColumn<VTypePair> col = new TooltipTableColumn<>(
                snapshots.get(i).toString() + " (" + Utilities.DELTA_CHAR + " Base)",
                "PV value when the snapshot was taken", 100);
            final int snapshotIndex = i;
            MenuItem removeItem = new MenuItem("Remove");
            removeItem.setOnAction(ev -> SaveRestoreService.getInstance().execute("Remove Snapshot", () -> {
                final List<TableEntry> entries = controller.removeSnapshot(snapshotIndex);
                final List<VSnapshot> snaps = controller.getAllSnapshots();
                final boolean show = controller.isShowReadbacks();
                final boolean showStored = controller.isShowStoredReadbacks();
                Platform.runLater(() -> updateTable(entries, snaps, show, showStored));
            }));
            MenuItem setAsBaseItem = new MenuItem("Set As Base");
            setAsBaseItem.setOnAction(ev -> SaveRestoreService.getInstance().execute("Set new base Snapshot", () -> {
                final List<TableEntry> entries = controller.setAsBase(snapshotIndex);
                final List<VSnapshot> snaps = controller.getAllSnapshots();
                final boolean show = controller.isShowReadbacks();
                final boolean showStored = controller.isShowStoredReadbacks();
                Platform.runLater(() -> updateTable(entries, snaps, show, showStored));
            }));
            final ContextMenu menu = new ContextMenu(removeItem, setAsBaseItem);
            col.label.setContextMenu(menu);
            col.setCellValueFactory(e -> e.getValue().compareValueProperty(snapshotIndex));
            col.setCellFactory(e -> new VTypeCellEditor<>());
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
                storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>());
                storedReadbackColumn.setEditable(false);
                storedValueColumn.getColumns().add(storedReadbackColumn);
            }
        }
        if (showReadback) {
            TableColumn<TableEntry, VType> readbackColumn = new TooltipTableColumn<>(
                "Live Readback (" + Utilities.DELTA_CHAR + " Live Setpoint)", "Current Readback value", 100);
            readbackColumn.setCellValueFactory(new PropertyValueFactory<>("readback"));
            readbackColumn.setCellFactory(e -> new VTypeCellEditor<>());
            readbackColumn.setEditable(false);
            getColumns().addAll(Arrays.asList(selectedColumn, idColumn, pvNameColumn, storedValueColumn,
                liveValueColumn, readbackColumn));
        } else {
            getColumns()
                .addAll(Arrays.asList(selectedColumn, idColumn, pvNameColumn, storedValueColumn, liveValueColumn));
        }
    }

    /**
     * Updates the table by setting new content.
     *
     * @param entries the table entries (rows) to set on the table
     * @param snapshots the snapshots which are currently displayed
     * @param showReadback true if readback column should be visible or false otherwise
     * @param showStoredReadback true if the stored readback value columns should be visible or false otherwise
     */
    public void updateTable(List<TableEntry> entries, List<VSnapshot> snapshots, boolean showReadback,
        boolean showStoredReadback) {
        getColumns().clear();
        uiSnapshots.clear();
        uiSnapshots.addAll(snapshots);
        if (snapshots.size() == 1) {
            createTableForSingleSnapshot(snapshots.get(0), showReadback, showStoredReadback);
        } else {
            createTableForMultipleSnapshots(snapshots, showReadback, showStoredReadback);
        }
        updateTableColumnTitles();
        updateTable(entries);
    }

    /**
     * Sets new table entries for this table.
     *
     * @param entries the entries to set
     */
    public void updateTable(List<TableEntry> entries) {
        final ObservableList<TableEntry> items = getItems();
        final boolean notHide = !controller.isHideEqualItems();
        items.clear();
        entries.forEach(e -> {
            // there is no harm if this is executed more than once, because only one listener is allowed
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
    public void addItem(TableEntry entry) {
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
            TableColumn<TableEntry, ?> column = ((TableColumn<TableEntry, ?>) getColumns().get(3));
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

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    @Override
    public ISelection getSelection() {
        int numSnapshots = controller.getNumberOfSnapshots();
        VSnapshot snapshot;
        if (numSnapshots == 1 || clickedColumn < 0) {
            snapshot = controller.getSnapshot(0);
        } else if (clickedColumn - 3 < numSnapshots) {
            snapshot = controller.getSnapshot(clickedColumn - 3);
        } else {
            snapshot = controller.getSnapshot(numSnapshots - 1);
        }
        if (snapshot == null) {
            return null;
        }
        Timestamp timestamp = snapshot.getTimestamp();
        if (timestamp == null) {
            List<ProcessVariable> list = new ArrayList<>();
            for (TableEntry e : selectionModelProperty().get().getSelectedItems()) {
                list.add(new ProcessVariable(e.pvNameProperty().get()));
            }
            return new StructuredSelection(list);
        } else {
            long time = timestamp.toDate().getTime();
            List<TimestampedPV> list = new ArrayList<>();
            for (TableEntry e : selectionModelProperty().get().getSelectedItems()) {
                list.add(new TimestampedPV(e.pvNameProperty().get(), time));
            }
            return new StructuredSelection(list);
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
    }
}
