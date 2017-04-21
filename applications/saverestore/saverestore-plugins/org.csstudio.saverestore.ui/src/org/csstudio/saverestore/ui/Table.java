/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.Utilities.VTypeComparison;
import org.csstudio.saverestore.data.VDisconnectedData;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.MultitypeTableCell;
import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.UnfocusableCheckBox;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
     * <code>TimestampTableCell</code> is a table cell for rendering the {@link Instant} objects in the table.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static class TimestampTableCell extends TableCell<TableEntry, Instant> {
        @Override
        protected void updateItem(Instant item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setStyle("");
            } else if (item == null) {
                setText("---");
                setStyle("");
            } else {
                setText(Utilities.timestampToLittleEndianString(item, true));
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
        private static final Image DISCONNECTED_IMAGE = new Image(
            SnapshotViewerEditor.class.getResourceAsStream("/icons/showerr_tsk.png"));
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
                            if (t.value instanceof VDisconnectedData) {
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
                        FXMessageDialog.openError(controller.getSnapshotReceiver().getShell(), "Editing Error",
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

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().remove("diff-cell");
            if (item == null || empty) {
                setText("");
                setTooltip(null);
                setGraphic(null);
            } else {
                if (item == VDisconnectedData.INSTANCE) {
                    setText(VDisconnectedData.DISCONNECTED);
                    setGraphic(new ImageView(DISCONNECTED_IMAGE));
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                    getStyleClass().add("diff-cell");
                } else if (item == VNoData.INSTANCE) {
                    setText(item.toString());
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                } else if (item instanceof VType) {
                    setText(Utilities.valueToString((VType) item));
                    setGraphic(null);
                    tooltip.setText(item.toString());
                    setTooltip(tooltip);
                } else if (item instanceof VTypePair) {
                    VTypePair pair = (VTypePair) item;
                    if (pair.value == VDisconnectedData.INSTANCE) {
                        setText(VDisconnectedData.DISCONNECTED);
                        if (pair.base != VDisconnectedData.INSTANCE) {
                            getStyleClass().add("diff-cell");
                        }
                        setGraphic(new ImageView(DISCONNECTED_IMAGE));
                    } else if (pair.value == VNoData.INSTANCE) {
                        setText(pair.value.toString());
                    } else {
                        VTypeComparison vtc = Utilities.valueToCompareString(pair.value, pair.base, pair.threshold);
                        setText(vtc.getString());
                        if (!vtc.isWithinThreshold()) {
                            getStyleClass().add("diff-cell");
                            setGraphic(new ImageView(WARNING_IMAGE));
                        }
                    }

                    tooltip.setText(item.toString());
                    setTooltip(tooltip);
                }
            }
        }
    }

    /**
     * A dedicated CellEditor for displaying delta only.
     * TODO can be simplified further
     * @author Kunal Shroff
     *
     * @param <T>
     */
    private static class VDeltaCellEditor<T> extends VTypeCellEditor<T> {

        private static final Image WARNING_IMAGE = new Image(
            SnapshotViewerEditor.class.getResourceAsStream("/icons/hprio_tsk.png"));
        private static final Image DISCONNECTED_IMAGE = new Image(
            SnapshotViewerEditor.class.getResourceAsStream("/icons/showerr_tsk.png"));
        private final Tooltip tooltip = new Tooltip();

        VDeltaCellEditor(SnapshotViewerController cntrl) {
            super(cntrl);
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().remove("diff-cell");
            if (item == null || empty) {
                setText("");
                setTooltip(null);
                setGraphic(null);
            } else {
                if (item == VDisconnectedData.INSTANCE) {
                    setText(VDisconnectedData.DISCONNECTED);
                    setGraphic(new ImageView(DISCONNECTED_IMAGE));
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                    getStyleClass().add("diff-cell");
                } else if (item == VNoData.INSTANCE) {
                    setText(item.toString());
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                } else if (item instanceof VTypePair) {
                    VTypePair pair = (VTypePair) item;
                    if (pair.value == VDisconnectedData.INSTANCE) {
                        setText(VDisconnectedData.DISCONNECTED);
                        if (pair.base != VDisconnectedData.INSTANCE) {
                            getStyleClass().add("diff-cell");
                        }
                        setGraphic(new ImageView(DISCONNECTED_IMAGE));
                    } else if (pair.value == VNoData.INSTANCE) {
                        setText(pair.value.toString());
                    } else {
                        VTypeComparison vtc = Utilities.deltaValueToString(pair.value, pair.base, pair.threshold);
                        setText(vtc.getString());
                        if (!vtc.isWithinThreshold()) {
                            getStyleClass().add("diff-cell");
                            setGraphic(new ImageView(WARNING_IMAGE));
                        }
                    }

                    tooltip.setText(item.toString());
                    setTooltip(tooltip);
                }
            }
        }
    }

    private static class VSetpointCellEditor<T> extends VTypeCellEditor<T> {

        private static final Image DISCONNECTED_IMAGE = new Image(
                SnapshotViewerEditor.class.getResourceAsStream("/icons/showerr_tsk.png"));
        private final Tooltip tooltip = new Tooltip();

        VSetpointCellEditor(SnapshotViewerController cntrl) {
            super(cntrl);
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().remove("diff-cell");
            if (item == null || empty) {
                setText("");
                setTooltip(null);
                setGraphic(null);
            } else {
                if (item == VDisconnectedData.INSTANCE) {
                    setText(VDisconnectedData.DISCONNECTED);
                    setGraphic(new ImageView(DISCONNECTED_IMAGE));
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                    getStyleClass().add("diff-cell");
                } else if (item == VNoData.INSTANCE) {
                    setText(item.toString());
                    tooltip.setText("No Value Available");
                    setTooltip(tooltip);
                } else if (item instanceof VType) {
                    setText(Utilities.valueToString((VType) item));
                    setGraphic(null);
                    tooltip.setText(item.toString());
                    setTooltip(tooltip);
                } else if (item instanceof VTypePair) {
                    VTypePair pair = (VTypePair) item;
                    if (pair.value == VDisconnectedData.INSTANCE) {
                        setText(VDisconnectedData.DISCONNECTED);
                        if (pair.base != VDisconnectedData.INSTANCE) {
                            getStyleClass().add("diff-cell");
                        }
                        setGraphic(new ImageView(DISCONNECTED_IMAGE));
                    } else if (pair.value == VNoData.INSTANCE) {
                        setText(pair.value.toString());
                    } else {
                        setText(Utilities.valueToString(pair.value));
                    }
                    tooltip.setText(pair.value.toString());
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
            label.setTooltip(new Tooltip(tooltip));
            label.setTextAlignment(TextAlignment.CENTER);
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
                String t = this.text;
                if (text.indexOf('\n') > 0) {
                    t = "*" + t.replaceFirst("\n", "*\n");
                } else {
                    t = "*" + t + "*";
                }
                label.setText(t);
            }
        }
    }

    /**
     * <code>SelectionTableColumn</code> is the table column for the first column in the table, which displays
     * a checkbox, whether the PV should be selected or not.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private class SelectionTableColumn extends TooltipTableColumn<Boolean> {
        SelectionTableColumn() {
            super("", "Include this PV when restoring values", 30, 30, false);
            setCellValueFactory(new PropertyValueFactory<>("selected"));
            //for those entries, which have a read-only property, disable the checkbox
            setCellFactory(column -> {
                TableCell<TableEntry, Boolean> cell = new CheckBoxTableCell<>(null,null);
                cell.itemProperty().addListener((a, o, n) -> {
                    cell.getStyleClass().remove("check-box-table-cell-disabled");
                    TableRow<?> row = cell.getTableRow();
                    if (row != null) {
                        TableEntry item = (TableEntry) row.getItem();
                        if (item != null) {
                            cell.setEditable(!item.readOnlyProperty().get());
                            if (item.readOnlyProperty().get()) {
                                cell.getStyleClass().add("check-box-table-cell-disabled");
                            }
                        }
                    }
                });
                return cell;
            });
            setEditable(true);
            setSortable(false);
            selectAllCheckBox = new UnfocusableCheckBox();
            selectAllCheckBox.setSelected(false);
            selectAllCheckBox.setOnAction(e -> getItems().stream().filter(te -> !te.readOnlyProperty().get())
                    .forEach(te -> te.selectedProperty().setValue(selectAllCheckBox.isSelected())));
            setGraphic(selectAllCheckBox);
            MenuItem inverseMI = new MenuItem("Inverse Selection");
            inverseMI.setOnAction(e -> getItems().stream().filter(te -> !te.readOnlyProperty().get())
                    .forEach(te -> te.selectedProperty().setValue(!te.selectedProperty().get())));
            final ContextMenu contextMenu = new ContextMenu(inverseMI);
            selectAllCheckBox.setOnMouseReleased(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(selectAllCheckBox, e.getScreenX(), e.getScreenY());
                }
            });
        }
    }

    private final List<VSnapshot> uiSnapshots = new ArrayList<>();
    private boolean showStoredReadbacks;
    private boolean showReadbacks;
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
                        int i = showReadbacks ? 4 : 3;
                        if (idx < 0) {
                            // it is one of the grouped stored values columns
                            idx = getColumns().get(i).getColumns().indexOf(columnAtMouse);
                            if (idx >= 0) {
                                idx += i;
                            }
                        } else {
                            // it is either one of the first 3 columns (do nothing) or one of the live columns
                            if (idx > i) {
                                idx = getColumns().get(i).getColumns().size() + idx - 1;
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
        List<TableColumn<TableEntry, ?>> list = new ArrayList<>(12);

        TableColumn<TableEntry, Boolean> selectedColumn = new SelectionTableColumn();
        list.add(selectedColumn);

        int width = FXUtilities.measureStringWidth("0000", Font.font(20));
        TableColumn<TableEntry, Integer> idColumn = new TooltipTableColumn<>("#",
            "The order number of the PV in the save set", width, width, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        list.add(idColumn);

        TableColumn<TableEntry, String> setpointPVName = new TooltipTableColumn<>("Setpoint\nPV Name",
            "The list of setpoint PV names defined by the snapshot or save set", 100);
        setpointPVName.setCellValueFactory(new PropertyValueFactory<>("pvName"));
        list.add(setpointPVName);
        if (showReadback) {
            TableColumn<TableEntry, String> readbackPVName = new TooltipTableColumn<>("Readback\nPV Name",
                "The list of readback PV names associated with the setpoints", 100);
            readbackPVName.setCellValueFactory(new PropertyValueFactory<>("readbackName"));
            list.add(readbackPVName);
        }

        width = FXUtilities.measureStringWidth("MM:MM:MM.MMM MMM MM M", null);
        TableColumn<TableEntry, Instant> timestampColumn = new TooltipTableColumn<>("Timestamp",
            "Timestamp of the setpoint value when the snapshot was taken", width, width, true);
        timestampColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, Instant>("timestamp"));
        timestampColumn.setCellFactory(c -> new TimestampTableCell());
        timestampColumn.setPrefWidth(width);
        list.add(timestampColumn);

        TableColumn<TableEntry, String> statusColumn = new TooltipTableColumn<>("Status",
            "Alarm status of the setpoint PV when the snapshot was taken", 100, 100, true);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        list.add(statusColumn);

        TableColumn<TableEntry, AlarmSeverity> severityColumn = new TooltipTableColumn<>("Severity",
            "Alarm severity of the setpoint PV when the snapshot was taken", 80, 80, false);
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        list.add(severityColumn);

        TableColumn<TableEntry, ?> storedValueBaseColumn = new TooltipTableColumn<>(
                "Stored Setpoint", "", -1);

        TableColumn<TableEntry, VType> storedValueColumn = new TooltipTableColumn<>(
            "Stored Setpoint",
            "Setpoint PV value when the snapshot was taken", 100);
        storedValueColumn.setCellValueFactory(new PropertyValueFactory<>("snapshotVal"));
        storedValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        storedValueColumn.setEditable(true);
        storedValueColumn.setOnEditCommit(e -> {
            ObjectProperty<VTypePair> value = e.getRowValue().valueProperty();
            value.setValue(new VTypePair(value.get().base, e.getNewValue(), value.get().threshold));
            controller.updateSnapshot(0, e.getRowValue());
            controller.resume();
        });

        storedValueBaseColumn.getColumns().add(storedValueColumn);
        // show deltas in separate column
        TableColumn<TableEntry, VTypePair> delta = new TooltipTableColumn<>(
                Utilities.DELTA_CHAR + " Live Setpoint",
                "", 100);
        delta.setCellValueFactory(e -> e.getValue().valueProperty());
        delta.setCellFactory(e -> new VDeltaCellEditor<>(controller));
        delta.setEditable(false);
        storedValueBaseColumn.getColumns().add(delta);

        list.add(storedValueBaseColumn);

        if (showStoredReadback) {
            TableColumn<TableEntry, VType> storedReadbackColumn = new TooltipTableColumn<>(
                "Stored Readback\n(" + Utilities.DELTA_CHAR + " Stored Setpoint)", "Stored Readback Value", 100);
            storedReadbackColumn.setCellValueFactory(new PropertyValueFactory<>("storedReadback"));
            storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            storedReadbackColumn.setEditable(false);
            list.add(storedReadbackColumn);
        }

        TableColumn<TableEntry, VType> liveValueColumn = new TooltipTableColumn<>("Live Setpoint", "Current PV Value",
            100);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        liveValueColumn.setEditable(false);
        list.add(liveValueColumn);

        if (showReadback) {
            TableColumn<TableEntry, VType> readbackColumn = new TooltipTableColumn<>(
                "Live Readback\n(" + Utilities.DELTA_CHAR + " Live Setpoint)", "Current Readback Value", 100);
            readbackColumn.setCellValueFactory(new PropertyValueFactory<>("readback"));
            readbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            readbackColumn.setEditable(false);
            list.add(readbackColumn);
        }
        getColumns().addAll(list);
    }

    private void createTableForMultipleSnapshots(List<VSnapshot> snapshots, boolean showReadback,
        boolean showStoredReadback) {
        List<TableColumn<TableEntry, ?>> list = new ArrayList<>(7);
        TableColumn<TableEntry, Boolean> selectedColumn = new SelectionTableColumn();
        list.add(selectedColumn);

        int width = FXUtilities.measureStringWidth("0000", Font.font(20));
        TableColumn<TableEntry, Integer> idColumn = new TooltipTableColumn<>("#",
            "The order number of the PV in the save set", width, width, false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        list.add(idColumn);

        TableColumn<TableEntry, String> setpointPVName = new TooltipTableColumn<>("Setpoint\nPV Name",
            "Union of setpoint PV names defined by all compared snapshots", 100);
        setpointPVName.setCellValueFactory(new PropertyValueFactory<>("pvName"));
        list.add(setpointPVName);
        if (showReadback) {
            TableColumn<TableEntry, String> readbackPVName = new TooltipTableColumn<>("Readback\nPV Name",
                "The list of readback PV names associated with the setpoints", 66);
            readbackPVName.setCellValueFactory(new PropertyValueFactory<>("readbackName"));
            list.add(readbackPVName);
        }

        TableColumn<TableEntry, ?> storedValueColumn = new TooltipTableColumn<>("Stored Values",
            "PV values when the snapshots were taken", -1);
        TableColumn<TableEntry, ?> baseCol = new TooltipTableColumn<>(
            "Base Setpoint",
            "Setpoint PV value when the base snapshot was taken", 33);

        TableColumn<TableEntry, VType> storedBaseSetpointValueColumn = new TooltipTableColumn<>(
            "Base Setpoint",
            "Base Setpoint PV value when the snapshot was taken", 100);
        storedBaseSetpointValueColumn.setCellValueFactory(new PropertyValueFactory<>("snapshotVal"));
        storedBaseSetpointValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        storedBaseSetpointValueColumn.setEditable(true);
        storedBaseSetpointValueColumn.setOnEditCommit(e -> {
            ObjectProperty<VTypePair> value = e.getRowValue().valueProperty();
            value.setValue(new VTypePair(value.get().base, e.getNewValue(), value.get().threshold));
            controller.updateSnapshot(0, e.getRowValue());
            controller.resume();
        });

        baseCol.getColumns().add(storedBaseSetpointValueColumn);
        // show deltas in separate column
        TableColumn<TableEntry, VTypePair> delta = new TooltipTableColumn<>(
                Utilities.DELTA_CHAR + " Live Setpoint",
                "", 100);
        delta.setCellValueFactory(e -> e.getValue().valueProperty());
        delta.setCellFactory(e -> new VDeltaCellEditor<>(controller));
        delta.setEditable(false);
        baseCol.getColumns().add(delta);

        storedValueColumn.getColumns().add(baseCol);

        if (showStoredReadback) {
            TableColumn<TableEntry, VTypePair> storedReadbackColumn = new TooltipTableColumn<>(
                "Base Readback\n(" + Utilities.DELTA_CHAR + " Base Setpoint)",
                "Stored Readback PV value when the base snapshot was taken", 100);
            storedReadbackColumn.setCellValueFactory(e -> e.getValue().storedReadbackProperty());
            storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            storedReadbackColumn.setEditable(false);
            storedValueColumn.getColumns().add(storedReadbackColumn);
        }
        for (int i = 1; i < snapshots.size(); i++) {
            final int snapshotIndex = i;
            String snapshotName = String.valueOf(snapshots.get(snapshotIndex));
            final ContextMenu menu = createContextMenu(snapshotIndex);

            TooltipTableColumn<VTypePair> baseSnapshotCol = new TooltipTableColumn<>(snapshotName,
                    "Setpoint PV value when the " + snapshotName + " snapshot was taken", 100);
            baseSnapshotCol.label.setContextMenu(menu);

            TooltipTableColumn<VTypePair> setpointValueCol = new TooltipTableColumn<>(
                    "Setpoint",
                    "Setpoint PV value when the " + snapshotName + " snapshot was taken", 66);
            setpointValueCol.label.setContextMenu(menu);
            setpointValueCol.setCellValueFactory(e -> e.getValue().compareValueProperty(snapshotIndex));
            setpointValueCol.setCellFactory(e -> new VSetpointCellEditor<>(controller));
            setpointValueCol.setEditable(true);
            setpointValueCol.setOnEditCommit(e -> {
                ObjectProperty<VTypePair> value = e.getRowValue().compareValueProperty(snapshotIndex);
                value.setValue(new VTypePair(value.get().base, e.getNewValue().value, value.get().threshold));
                controller.updateSnapshot(snapshotIndex, e.getRowValue());
                controller.resume();
            });
            setpointValueCol.label.setOnMouseReleased(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    menu.show(setpointValueCol.label, e.getScreenX(), e.getScreenY());
                }
            });
            baseSnapshotCol.getColumns().add(setpointValueCol);

            TooltipTableColumn<VTypePair> deltaCol = new TooltipTableColumn<>(
                 Utilities.DELTA_CHAR + " Base Setpoint",
                "Setpoint PV value when the " + snapshotName + " snapshot was taken", 50);
            deltaCol.label.setContextMenu(menu);
            deltaCol.setCellValueFactory(e -> e.getValue().compareValueProperty(snapshotIndex));
            deltaCol.setCellFactory(e -> new VDeltaCellEditor<>(controller));
            deltaCol.setEditable(false);
            deltaCol.label.setOnMouseReleased(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    menu.show(deltaCol.label, e.getScreenX(), e.getScreenY());
                }
            });
            baseSnapshotCol.getColumns().add(deltaCol);
            storedValueColumn.getColumns().add(baseSnapshotCol);

            if (showStoredReadback) {
                TableColumn<TableEntry, VTypePair> storedReadbackColumn = new TooltipTableColumn<>(
                    "Readback\n(" + Utilities.DELTA_CHAR + " Setpoint)", "Stored Readback value", 100);
                storedReadbackColumn.setEditable(false);
                storedReadbackColumn
                    .setCellValueFactory(e -> e.getValue().compareStoredReadbackProperty(snapshotIndex));
                storedReadbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
                storedReadbackColumn.setEditable(false);
                storedValueColumn.getColumns().add(storedReadbackColumn);
            }
        }
        list.add(storedValueColumn);
        TableColumn<TableEntry, VType> liveValueColumn = new TooltipTableColumn<>("Live Setpoint",
            "Current Setpoint value", 100);
        liveValueColumn.setCellValueFactory(new PropertyValueFactory<>("liveValue"));
        liveValueColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
        liveValueColumn.setEditable(false);
        list.add(liveValueColumn);
        if (showReadback) {
            TableColumn<TableEntry, VType> readbackColumn = new TooltipTableColumn<>(
                "Live Readback\n(" + Utilities.DELTA_CHAR + " Live Setpoint)", "Current Readback Value", 100);
            readbackColumn.setCellValueFactory(new PropertyValueFactory<>("readback"));
            readbackColumn.setCellFactory(e -> new VTypeCellEditor<>(controller));
            readbackColumn.setEditable(false);
            list.add(readbackColumn);
        }
        getColumns().addAll(list);
    }

    private ContextMenu createContextMenu(final int snapshotIndex) {
        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(ev -> SaveRestoreService.getInstance().execute("Remove Snapshot",
            () -> update(controller.removeSnapshot(snapshotIndex))));
        MenuItem setAsBaseItem = new MenuItem("Set As Base");
        setAsBaseItem.setOnAction(ev -> SaveRestoreService.getInstance().execute("Set new base Snapshot",
            () -> update(controller.setAsBase(snapshotIndex))));
        MenuItem moveToNewEditor = new MenuItem("Move To New Editor");
        moveToNewEditor.setOnAction(ev -> SaveRestoreService.getInstance().execute("Open Snapshot",
            () -> update(controller.moveSnapshotToNewEditor(snapshotIndex))));
        return new ContextMenu(removeItem, setAsBaseItem, new SeparatorMenuItem(), moveToNewEditor);
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
        this.showReadbacks = showReadback;
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
     * Removes the given entry from this table.
     *
     * @param entry the entry to remove
     */
    void removeItem(TableEntry entry) {
        getItems().remove(entry);
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
        int i = showReadbacks ? 4 : 3;
        if (numSnapshots == 1 || clickedColumn <= i) {
            snapshot = uiSnapshots.get(0);
        } else {
            int subColumns = getColumns().get(i).getColumns().size();
            if (2 + subColumns < clickedColumn) {
                // clicked on one of the live columns - in this case select the right most snapshot
                snapshot = uiSnapshots.get(numSnapshots - 1);
            } else {
                int clickedSubColumn = clickedColumn - i;
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
        //if there is no PV selected, return null selection
        if (selectionModelProperty().get().getSelectedItems().isEmpty()) {
            return null;
        }
        // if snapshot was found, use its timestamp and create timestamped PVs
        Instant timestamp = snapshot.getTimestamp();
        if (timestamp == null) {
            return new StructuredSelection(selectionModelProperty().get().getSelectedItems().stream()
                .map(e -> new ProcessVariable(e.pvNameProperty().get())).collect(Collectors.toList()));
        } else {
            long time = timestamp.toEpochMilli();
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
            // in case of a single snapshot it is easy
            boolean readback = showStoredReadbacks && clickedColumn == 7
                || showReadbacks && clickedColumn == getColumns().size() - 1;
            boolean live = showStoredReadbacks ? clickedColumn > 7 : clickedColumn > 6;
            Object obj = getColumns().get(clickedColumn).getCellData(clickedRow);
            return extractValue(obj, clickedRow, live ? -1 : 0, readback);
        } else {
            int i = showReadbacks ? 4 : 3;
            if (clickedColumn <= i) {
                Object obj = getColumns().get(i).getColumns().get(0).getCellData(clickedRow);
                return extractValue(obj, clickedRow, 0, false);
            } else {
                int subColumns = getColumns().get(i).getColumns().size();
                if (clickedColumn < subColumns + i) {
                    // one of the subcolumns were clicked, extract the data and find the corresponding snapshot
                    int col = clickedColumn - i;
                    Object obj = getColumns().get(i).getColumns().get(col).getCellData(clickedRow);
                    // if stored readbacks are displayed, there are twice the number of columns as there are snapshots
                    // snapshot is only provided for the setpoints, we ignore the readbacks
                    boolean readback = showStoredReadbacks && col % 2 == 1;
                    col = showStoredReadbacks ? col / 2 : col;
                    return extractValue(obj, clickedRow, col, readback);
                } else {
                    // live data, no snapshot associated
                    int col = clickedColumn - subColumns + 1;
                    boolean readback = showReadbacks && col == getColumns().size() - 1;
                    Object obj = getColumns().get(col).getCellData(clickedRow);
                    return extractValue(obj, clickedRow, -1, readback);
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
     * @param snapshotIndex the index which defines the clicked snapshot
     * @return the value name pair if the cell data is value or null if cell data is anything else.
     */
    private VTypeNamePair extractValue(Object cellData, int row, int snapshotIndex, boolean readback) {
        VType value;
        if (cellData instanceof VType) {
            value = (VType) cellData;
        } else if (cellData instanceof VTypePair) {
            value = ((VTypePair) cellData).value;
        } else {
            value = null;
        }
        if (getItems().size() > clickedRow) {
            TableEntry entry = getItems().get(clickedRow);
            VSnapshot snapshot = snapshotIndex > -1 ? uiSnapshots.get(snapshotIndex) : null;
            String name = (readback ? entry.readbackNameProperty() : entry.pvNameProperty()).get();
            return new VTypeNamePair(value, name, snapshot, readback, entry);
        } else {
            return null;
        }
    }
}
