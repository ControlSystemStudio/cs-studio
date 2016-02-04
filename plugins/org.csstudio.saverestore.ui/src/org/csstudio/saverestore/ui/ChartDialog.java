package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.List;

import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.ui.fx.util.FXCanvasMaker;
import org.csstudio.ui.fx.util.FXUtilities;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VNumberArray;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
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

/**
 *
 * <code>ChartDialog</code> is a dialog which displays the values of a {@link VNumberArray} in a simple line chart.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ChartDialog extends Dialog {

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
        new FXCanvasMaker(parent, this::createFXButtonBar);
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
        new FXCanvasMaker(composite, this::getScene);
        applyDialogFont(composite);
        return composite;
    }

    private Scene getScene(Composite parent) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Array Index");
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle(value.name + " @ " + ((VNumberArray) value.value).getTimestamp().toDate());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(value.name);
        ListNumber data = ((VNumberArray) value.value).getData();
        List<Data<Number, Number>> list = series.getData();
        for (int i = 0; i < data.size(); i++) {
            list.add(new Data<>(i, data.getDouble(i)));
        }
        lineChart.getData().add(series);
        lineChart.getStylesheets()
            .add(SnapshotViewerEditor.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());
        lineChart.setStyle(FXUtilities.toBackgroundColorStyle(parent.getBackground()));
        lineChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        for (XYChart.Data<Number, Number> d : list) {
            Tooltip.install(d.getNode(),
                new Tooltip(String.format("(%d, %3.2f)", d.getXValue().longValue(), d.getYValue().doubleValue())));
        }
        return new Scene(lineChart, 600, 400);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#getInitialLocation(org.eclipse.swt.graphics.Point)
     */
    @Override
    protected Point getInitialLocation(Point initialSize) {
        Composite theParent = getShell().getParent();

        Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
        if (theParent != null) {
            monitor = theParent.getMonitor();
        }

        Rectangle monitorBounds = monitor.getClientArea();
        Point centerPoint;
        if (theParent == null) {
            centerPoint = Geometry.centerPoint(monitorBounds);
        } else {
            centerPoint = Geometry.centerPoint(theParent.getBounds());
        }

        return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y,
            Math.min(centerPoint.y - (initialSize.y / 2), monitorBounds.y + monitorBounds.height - initialSize.y)));
    }
}
