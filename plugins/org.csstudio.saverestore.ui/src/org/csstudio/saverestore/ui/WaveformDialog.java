package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.ZoomableLineChart;
import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.ListBoolean;
import org.diirt.util.array.ListByte;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListNumber;
import org.diirt.util.array.ListShort;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Array;
import org.diirt.vtype.Time;
import org.diirt.vtype.VBooleanArray;
import org.diirt.vtype.VByteArray;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VEnumArray;
import org.diirt.vtype.VFloatArray;
import org.diirt.vtype.VIntArray;
import org.diirt.vtype.VLongArray;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VShortArray;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 *
 * <code>WaveformDialog</code> is a dialog which displays the values of an {@link Array} type PVs in a table and in
 * a simple line chart. The dialog allows user to change the individual values in the array and then either confirm
 * or cancel the changes. The change that was done is applied to the snapshot if the snapshot is still opened in
 * the controller.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WaveformDialog extends Dialog {

    /**
     * <code>LabelFormatter</code> is the chart horizontal axis formatter, which shows only the integer part of the
     * value.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static class LabelFormatter extends StringConverter<Number> {
        @Override
        public String toString(Number object) {
            return String.valueOf(object.longValue());
        }

        @Override
        public Number fromString(String string) {
            return Long.parseLong(string);
        }
    }

    /**
     *
     * <code>TableValue</code> as an internal holder for the items in the table. To satisfy javaFX, this guy and its
     * methods have to be public!
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static class TableValue {
        private SimpleIntegerProperty indexProperty = new SimpleIntegerProperty(this, "index", 0);
        private SimpleObjectProperty<Object> valueProperty = new SimpleObjectProperty<>(this, "value", null);
        private final Object orgValue;

        TableValue(int index, Object value) {
            this.orgValue = value;
            indexProperty.set(index);
            valueProperty.set(value);
        }

        public SimpleIntegerProperty indexProperty() {
            return indexProperty;
        }

        public SimpleObjectProperty<Object> valueProperty() {
            return valueProperty;
        }

        Object getOrgValue() {
            return orgValue;
        }

        Object getNewValue() {
            return valueProperty.get();
        }
    }

    /**
     *
     * <code>ValueCell</code> is used for displaying the values in the value column in the table. This cell can
     * transform between strings and the underlying object types to allow editing of values.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static class ValueCell extends TextFieldTableCell<TableValue, Object> {
        private final Array type;

        ValueCell(Array value) {
            this.type = value;
            setConverter(new StringConverter<Object>() {
                @Override
                public String toString(Object object) {
                    return String.valueOf(object);
                }

                @Override
                public Object fromString(String string) {
                    if (type instanceof VNumberArray) {
                        try {
                            if (type instanceof VDoubleArray) {
                                return Double.parseDouble(string);
                            } else if (type instanceof VFloatArray) {
                                return Float.parseFloat(string);
                            } else if (type instanceof VLongArray) {
                                return Long.parseLong(string);
                            } else if (type instanceof VIntArray) {
                                return Integer.parseInt(string);
                            } else if (type instanceof VShortArray) {
                                return Short.parseShort(string);
                            } else if (type instanceof VByteArray) {
                                return Byte.parseByte(string);
                            }
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    } else if (type instanceof VBooleanArray) {
                        return Boolean.valueOf(string);
                    } else if (type instanceof VEnumArray) {
                        return string;
                    } else if (type instanceof VStringArray) {
                        return string;
                    }
                    return getItem();
                }
            });
        }
    }

    private static final int SYMBOLS_BOUNDARY = 30;
    private static final String CLOSE_LABEL = IDialogConstants.CLOSE_LABEL.replace("&", "");
    private static final String APPLY_LABEL = "Apply";
    private final VTypeNamePair value;
    private final SnapshotViewerController controller;
    private XYChart.Series<Number, Number> series;
    private Button applyButton;
    private List<TableValue> data;
    private final Map<String, Integer> hashes = new HashMap<>();

    /**
     * Creates a passive dialog that shows a table and chart of an array.
     *
     * @param parentShell the parent shell
     * @param value the value to display
     * @param controller the controller to which value will be set when confirmed
     */
    public WaveformDialog(Shell parentShell, VTypeNamePair value, SnapshotViewerController controller) {
        super(parentShell);
        setBlockOnOpen(false);
        this.value = value;
        this.controller = controller;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        VType v = (VType) toArrayValue(data, (Array) value.value);
        controller.updateSnapshot(value.snapshot, value.name, v);
        super.okPressed();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (value != null) {
            shell.setText(value.name);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns = 1;
        FXUtilities.createFXBridge(parent, this::createFXButtonBar);
    }

    private Scene createFXButtonBar(Composite parent) {
        applyButton = new Button(APPLY_LABEL);
        applyButton.setOnAction(e -> buttonPressed(IDialogConstants.OK_ID));
        applyButton.setDisable(true);
        Button closeButton = new Button(CLOSE_LABEL);
        closeButton.setOnAction(e -> buttonPressed(IDialogConstants.CANCEL_ID));
        GridPane pane = new GridPane();
        pane.setHgap(10);
        setGridConstraints(applyButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(closeButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        pane.add(applyButton, 0, 0);
        pane.add(closeButton, 1, 0);
        return new Scene(pane);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        FXUtilities.createFXBridge(composite, this::getScene);
        applyDialogFont(composite);
        return composite;
    }

    private Scene getScene(Composite parent) {
        TabPane tabPane = new TabPane(new Tab("Chart", getChartNode(parent)), new Tab("Table", getTableNode()));
        tabPane.getStylesheets()
            .add(SnapshotViewerEditor.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());
        tabPane.setStyle(FXUtilities.toBackgroundColorStyle(parent.getBackground()));
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        return new Scene(tabPane, 600, 400);
    }

    private Node getTableNode() {
        data = fillInData((Array) value.value);
        TableView<TableValue> table = new TableView<>();
        table.setEditable(value.snapshot != null && !value.readback);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<TableValue, Integer> indexColumn = new TableColumn<>("Index");
        indexColumn.setEditable(false);
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        indexColumn.setMaxWidth(80);
        indexColumn.setMinWidth(80);
        indexColumn.setPrefWidth(80);
        TableColumn<TableValue, Object> valueColumn = new TableColumn<>("Value");
        valueColumn.setEditable(true);
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setCellFactory(e -> new ValueCell((Array) value.value));
        valueColumn.setOnEditCommit(e -> {
            Object newValue = e.getNewValue();
            if (newValue == null || newValue.equals(e.getOldValue())) {
                return;
            }
            int index = e.getRowValue().indexProperty.get();
            e.getRowValue().valueProperty().setValue(newValue);
            Data<Number, Number> d = new Data<>(index, toChartNumberValue(newValue));
            series.getData().set(index, d);
            if (series.getData().size() < SYMBOLS_BOUNDARY) {
                if (newValue instanceof Number || newValue instanceof Boolean) {
                    Tooltip.install(d.getNode(), new Tooltip(
                        String.format("(%d, %3.2f)", d.getXValue().longValue(), d.getYValue().doubleValue())));
                } else {
                    Tooltip.install(d.getNode(),
                        new Tooltip(String.format("(%d, %s)", d.getXValue().longValue(), String.valueOf(newValue))));
                }
            }
            applyButton.setDisable(false);
        });
        table.getColumns().add(indexColumn);
        table.getColumns().add(valueColumn);
        table.setItems(FXCollections.observableList(data));
        return table;
    }

    private Node getChartNode(Composite parent) {
        ZoomableLineChart chart = new ZoomableLineChart(
            Optional.of(value.name + " @ " + ((Time) value.value).getTimestamp().toDate()), Optional.of("Array Index"),
            Optional.empty());
        series = new XYChart.Series<>();
        series.setName(value.name);
        List<Data<Number, Number>> list = series.getData();
        List<String> stringData = fillInData(list, (Array) value.value);
        boolean createSymbols = list.size() < SYMBOLS_BOUNDARY;
        LineChart<Number, Number> lineChart = chart.getChart();
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(createSymbols);
        lineChart.getData().add(series);
        ((NumberAxis) lineChart.getXAxis()).setTickLabelFormatter(new LabelFormatter());
        if (createSymbols) {
            // this has to be executed after the series has been added to the chart so that the nodes already exist
            if (stringData == null) {
                for (XYChart.Data<Number, Number> d : list) {
                    Tooltip.install(d.getNode(), new Tooltip(
                        String.format("(%d, %3.2f)", d.getXValue().longValue(), d.getYValue().doubleValue())));
                }
            } else {
                XYChart.Data<Number, Number> d;
                for (int i = 0; i < list.size(); i++) {
                    d = list.get(i);
                    Tooltip.install(d.getNode(),
                        new Tooltip(String.format("(%d, %s)", d.getXValue().longValue(), stringData.get(i))));
                }
            }
        }
        lineChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return chart;
    }

    /**
     * Fill in the chart data points from the array value. If the value is a string or enum type the method returns the
     * list of string values, which can be used as tooltips.
     *
     * @param list the list to fill
     * @param value the source of data
     * @return tooltip values if data is string or enum, or null otherwise
     */
    private List<String> fillInData(List<Data<Number, Number>> list, Array value) {
        if (value instanceof VNumberArray) {
            ListNumber data = ((VNumberArray) value).getData();
            for (int i = 0; i < data.size(); i++) {
                list.add(new Data<>(i, data.getDouble(i)));
            }
            return null;
        } else if (value instanceof VEnumArray) {
            List<String> data = ((VEnumArray) value).getData();
            List<String> labels = ((VEnumArray) value).getLabels();
            for (int i = 0; i < data.size(); i++) {
                list.add(new Data<>(i, labels.indexOf(data.get(i))));
            }
            return data;
        } else if (value instanceof VBooleanArray) {
            ListBoolean data = ((VBooleanArray) value).getData();
            for (int i = 0; i < data.size(); i++) {
                list.add(new Data<>(i, data.getBoolean(i) ? 1 : 0));
            }
            return null;
        } else if (value instanceof VStringArray) {
            List<String> data = ((VStringArray) value).getData();
            String s;
            Integer hash;
            for (int i = 0; i < data.size(); i++) {
                s = data.get(i);
                hash = hashes.get(s);
                if (hash == null) {
                    hash = hashes.size();
                    hashes.put(s, hash);
                }
                list.add(new Data<>(i, hash));
            }
            return data;
        }
        return null;
    }

    /**
     * Transforms the given data to a number that can be displayed on the chart.
     *
     * @param data the data
     * @return a number representing the data
     */
    private Number toChartNumberValue(Object data) {
        if (data instanceof Number) {
            return (Number) data;
        } else if (data instanceof Boolean) {
            return (Boolean) data ? 1 : 0;
        } else if (data instanceof String) {
            if (value.value instanceof VEnumArray) {
                return ((VEnumArray) value.value).getLabels().indexOf(data);
            } else if (value.value instanceof VStringArray) {
                Integer v = hashes.get(data);
                if (v == null) {
                    v = hashes.size();
                    hashes.put((String) data, v);
                }
                return v;
            }
        }
        return Double.NaN;
    }

    /**
     * Extract the contents of the array and fill in a list with {@link TableValue}s representing the values in the
     * given array.
     *
     * @param value the source data
     * @return a list of table values for the given source
     */
    private static List<TableValue> fillInData(Array value) {
        if (value instanceof VNumberArray) {
            ListNumber data = ((VNumberArray) value).getData();
            int length = data.size();
            List<TableValue> dataList = new ArrayList<>(length);
            if (data instanceof ListLong) {
                for (int i = 0; i < length; i++) {
                    dataList.add(new TableValue(i, data.getLong(i)));
                }
            } else if (data instanceof ListFloat) {
                for (int i = 0; i < length; i++) {
                    dataList.add(new TableValue(i, data.getFloat(i)));
                }
            } else if (data instanceof ListDouble) {
                for (int i = 0; i < length; i++) {
                    dataList.add(new TableValue(i, data.getDouble(i)));
                }
            } else if (data instanceof ListInt) {
                for (int i = 0; i < length; i++) {
                    dataList.add(new TableValue(i, data.getInt(i)));
                }
            } else if (data instanceof ListShort) {
                for (int i = 0; i < length; i++) {
                    dataList.add(new TableValue(i, data.getShort(i)));
                }
            } else if (data instanceof ListByte) {
                for (int i = 0; i < length; i++) {
                    dataList.add(new TableValue(i, data.getByte(i)));
                }
            }
            return dataList;
        } else if (value instanceof VEnumArray) {
            List<String> data = ((VEnumArray) value).getData();
            int length = data.size();
            List<TableValue> dataList = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                dataList.add(new TableValue(i, data.get(i)));
            }
            return dataList;
        } else if (value instanceof VBooleanArray) {
            ListBoolean data = ((VBooleanArray) value).getData();
            int length = data.size();
            List<TableValue> dataList = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                dataList.add(new TableValue(i, data.getBoolean(i)));
            }
            return dataList;
        } else if (value instanceof VStringArray) {
            List<String> data = ((VStringArray) value).getData();
            int length = data.size();
            List<TableValue> dataList = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                dataList.add(new TableValue(i, data.get(i)));
            }
            return dataList;
        } else {
            return new ArrayList<>(0);
        }
    }

    /**
     * Transforms the table values to a VType Array of the same type as the original data.
     *
     * @param data the data to transform
     * @param original the original type to transform to
     * @return the VType array
     */
    private static VType toArrayValue(List<TableValue> data, Array original) {
        Alarm alarm = ValueFactory.newAlarm(AlarmSeverity.NONE, "USER DEFINED");
        Time time = ValueFactory.timeNow();
        if (original instanceof VNumberArray) {
            int length = data.size();
            if (original instanceof VLongArray) {
                long[] d = new long[length];
                for (int i = 0; i < length; i++) {
                    d[i] = (Long) data.get(i).getNewValue();
                }
                ListLong ad = new ArrayLong(d);
                return ValueFactory.newVLongArray(ad, alarm, time, (VNumberArray) original);
            } else if (original instanceof VFloatArray) {
                float[] d = new float[length];
                for (int i = 0; i < length; i++) {
                    d[i] = (Float) data.get(i).getNewValue();
                }
                ListFloat ad = new ArrayFloat(d);
                return ValueFactory.newVFloatArray(ad, alarm, time, (VNumberArray) original);
            } else if (original instanceof VDoubleArray) {
                double[] d = new double[length];
                for (int i = 0; i < length; i++) {
                    d[i] = (Double) data.get(i).getNewValue();
                }
                ListDouble ad = new ArrayDouble(d);
                return ValueFactory.newVDoubleArray(ad, alarm, time, (VNumberArray) original);
            } else if (original instanceof VIntArray) {
                int[] d = new int[length];
                for (int i = 0; i < length; i++) {
                    d[i] = (Integer) data.get(i).getNewValue();
                }
                ListInt ad = new ArrayInt(d);
                return ValueFactory.newVIntArray(ad, alarm, time, (VNumberArray) original);
            } else if (original instanceof VShortArray) {
                short[] d = new short[length];
                for (int i = 0; i < length; i++) {
                    d[i] = (Short) data.get(i).getNewValue();
                }
                ListShort ad = new ArrayShort(d);
                return ValueFactory.newVShortArray(ad, alarm, time, (VNumberArray) original);
            } else if (original instanceof VByteArray) {
                byte[] d = new byte[length];
                for (int i = 0; i < length; i++) {
                    d[i] = (Byte) data.get(i).getNewValue();
                }
                ListByte ad = new ArrayByte(d);
                return ValueFactory.newVNumberArray(ad, alarm, time, (VNumberArray) original);
            }
        } else if (original instanceof VEnumArray) {
            List<String> labels = ((VEnumArray) original).getLabels();
            int length = data.size();
            int[] d = new int[length];
            for (int i = 0; i < length; i++) {
                d[i] = labels.indexOf((String) data.get(i).getNewValue());
            }
            ListInt indexes = new ArrayInt(d);
            return ValueFactory.newVEnumArray(indexes, labels, alarm, time);
        } else if (original instanceof VBooleanArray) {
            int length = data.size();
            boolean[] d = new boolean[length];
            for (int i = 0; i < length; i++) {
                d[i] = (Boolean) data.get(i).getNewValue();
            }
            ListBoolean ad = new ArrayBoolean(d);
            return ValueFactory.newVBooleanArray(ad, alarm, time);
        } else if (original instanceof VStringArray) {
            int length = data.size();
            List<String> d = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                d.add((String) data.get(i).getNewValue());
            }
            return ValueFactory.newVStringArray(d, alarm, time);
        }
        return (VType) original;
    }
}
