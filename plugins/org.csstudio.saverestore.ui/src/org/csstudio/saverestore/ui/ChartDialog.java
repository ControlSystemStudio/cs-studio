package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.ZoomableLineChart;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VNumberArray;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 *
 * <code>ChartDialog</code> is a dialog which displays the values of a {@link VNumberArray} in a simple line chart.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ChartDialog extends Dialog {

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

    private static final String CLOSE_LABEL = IDialogConstants.CLOSE_LABEL.replace("&", "");
    private final VTypeNamePair value;

    /**
     * Creates a passive dialog that shows a chart of a number array.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param value the value to display
     */
    public ChartDialog(Shell parentShell, VTypeNamePair value) {
        super(parentShell);
        this.value = value;
        setBlockOnOpen(false);
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
        Button closeButton = new Button(CLOSE_LABEL);
        closeButton.setOnAction(e -> buttonPressed(IDialogConstants.OK_ID));
        GridPane pane = new GridPane();
        pane.setHgap(10);
        setGridConstraints(closeButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        pane.add(closeButton, 0, 0);
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
        ZoomableLineChart chart = new ZoomableLineChart(
            Optional.of(value.name + " @ " + ((VNumberArray) value.value).getTimestamp().toDate()),
            Optional.of("Array Index"), Optional.empty());

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(value.name);
        ListNumber data = ((VNumberArray) value.value).getData();
        List<Data<Number, Number>> list = series.getData();
        for (int i = 0; i < data.size(); i++) {
            list.add(new Data<>(i, data.getDouble(i)));
        }
        boolean createSymbols = data.size() < 30;
        LineChart<Number, Number> lineChart = chart.getChart();
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(createSymbols);
        lineChart.getData().add(series);
        ((NumberAxis) lineChart.getXAxis()).setTickLabelFormatter(new LabelFormatter());
        if (createSymbols) {
            // this has to be executed after the series has been added to the chart so that the nodes already exist
            for (XYChart.Data<Number, Number> d : list) {
                Tooltip.install(d.getNode(),
                    new Tooltip(String.format("(%d, %3.2f)", d.getXValue().longValue(), d.getYValue().doubleValue())));
            }
        }
        lineChart.getStylesheets()
            .add(SnapshotViewerEditor.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());
        lineChart.setStyle(FXUtilities.toBackgroundColorStyle(parent.getBackground()));
        lineChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return new Scene(chart, 600, 400);
    }
}
