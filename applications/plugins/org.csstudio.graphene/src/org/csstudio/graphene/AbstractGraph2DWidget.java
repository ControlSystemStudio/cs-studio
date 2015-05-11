/**
 *
 */
package org.csstudio.graphene;

import static org.epics.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.composites.BeanComposite;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.RangeListener;
import org.csstudio.ui.util.widgets.StartEndRangeWidget;
import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;
import org.epics.graphene.Graph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.graphene.Graph2DExpression;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.GraphDataRange;

/**
 * @author shroffk
 *
 */
public abstract class AbstractGraph2DWidget<U extends Graph2DRendererUpdate<U>, T extends Graph2DExpression<U>>
        extends BeanComposite implements ConfigurableWidget {

    private VImageDisplay imageDisplay;
    private T graph;
    private ErrorBar errorBar;
    private boolean resizableAxis = true;
    private StartEndRangeWidget yRangeControl;
    private StartEndRangeWidget xRangeControl;

    public AbstractGraph2DWidget(Composite parent, int style) {
        super(parent, style);

        // Close PV on dispose
        addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (pv != null) {
                    pv.close();
                    pv = null;
                }
            }
        });

        setLayout(new FormLayout());

        errorBar = new ErrorBar(this, SWT.NONE);
        FormData fd_errorBar = new FormData();
        fd_errorBar.left = new FormAttachment(0, 2);
        fd_errorBar.right = new FormAttachment(100, -2);
        fd_errorBar.top = new FormAttachment(0, 2);
        errorBar.setLayoutData(fd_errorBar);

        errorBar.setMarginBottom(5);

        yRangeControl = new StartEndRangeWidget(this, SWT.NONE);
        FormData fd_yRangeControl = new FormData();
        fd_yRangeControl.top = new FormAttachment(errorBar, 2);
        fd_yRangeControl.left = new FormAttachment(0, 2);
        fd_yRangeControl.bottom = new FormAttachment(100, -15);
        fd_yRangeControl.right = new FormAttachment(0, 13);
        yRangeControl.setLayoutData(fd_yRangeControl);
        yRangeControl.setOrientation(ORIENTATION.VERTICAL);
        yRangeControl.addRangeListener(new RangeListener() {

            @Override
            public void rangeChanged() {
                if (graph != null && isResizableAxis() && !yRangeEditing) {
                    if (yRangeControl.isRangeSet()) {
                        if (yRangeControl.getSelectedMin() == yRangeControl
                                .getMin()
                                && yRangeControl.getSelectedMax() == yRangeControl
                                        .getMax()) {
                            yRangeModified = false;
                            graph.update(graph.newUpdate().yAxisRange(getYAxisRange()));
                        } else {
                            yRangeModified = true;
                            double invert = yRangeControl.getMin() + yRangeControl.getMax();
                            graph.update(graph.newUpdate().yAxisRange(
                                    AxisRanges.fixed(
                                            invert - yRangeControl.getSelectedMax(),
                                            invert - yRangeControl.getSelectedMin())));
                        }
                    } else {
                        graph.update(graph.newUpdate().yAxisRange(
                                getYAxisRange()));
                    }
                }
            }
        });
        yRangeControl.setVisible(resizableAxis);

        imageDisplay = new VImageDisplay(this);
        FormData fd_imageDisplay = new FormData();
        fd_imageDisplay.top = new FormAttachment(errorBar, 2);
        fd_imageDisplay.right = new FormAttachment(100, -2);
        fd_imageDisplay.left = new FormAttachment(yRangeControl, 2);
        imageDisplay.setLayoutData(fd_imageDisplay);
        imageDisplay.setStretched(SWT.HORIZONTAL | SWT.VERTICAL);

        imageDisplay.addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                if (graph != null) {
                    graph.update(graph.newUpdate()
                            .imageHeight(imageDisplay.getSize().y)
                            .imageWidth(imageDisplay.getSize().x));
                }
            }

            @Override
            public void controlMoved(ControlEvent e) {
                // Nothing to do
            }
        });

        xRangeControl = new StartEndRangeWidget(this, SWT.NONE);
        fd_imageDisplay.bottom = new FormAttachment(xRangeControl, -2);
        FormData fd_xRangeControl = new FormData();
        fd_xRangeControl.left = new FormAttachment(0, 15);
        fd_xRangeControl.top = new FormAttachment(100, -13);
        fd_xRangeControl.right = new FormAttachment(100, -2);
        fd_xRangeControl.bottom = new FormAttachment(100, -2);
        xRangeControl.setLayoutData(fd_xRangeControl);
        xRangeControl.addRangeListener(new RangeListener() {

            @Override
            public void rangeChanged() {
                if (graph != null && isResizableAxis() && !xRangeEditing) {
                    if (xRangeControl.isRangeSet()) {
                        if(xRangeControl.getSelectedMin() == xRangeControl.getMin() && xRangeControl.getSelectedMax() == xRangeControl.getMax()){
                            xRangeModified = false;
                            graph.update(graph.newUpdate().xAxisRange(getXAxisRange()));
                        }else{
                            xRangeModified = true;
                            graph.update(graph.newUpdate().xAxisRange(
                                    AxisRanges.fixed(
                                            xRangeControl.getSelectedMin(),
                                            xRangeControl.getSelectedMax())));
                        }
                    } else {
                        graph.update(graph.newUpdate().xAxisRange(getXAxisRange()));
                    }
                }
            }
        });
        xRangeControl.setVisible(resizableAxis);

        final List<String> reconnectionProperties = Arrays.asList("dataFormula", "xColumnFormula", "yColumnFormula", "tooltipFormula");
        final List<String> updateProperties = Arrays.asList("xAxisRange", "yAxisRange");
        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (reconnectionProperties.contains(event.getPropertyName())) {
                    reconnect();
                } else if (updateProperties.contains(event.getPropertyName())) {
                    updateGraph();
                } else if (event.getPropertyName().equals("resizableAxis")) {
                    xRangeControl.setVisible(resizableAxis);
                    yRangeControl.setVisible(resizableAxis);
                    redraw();
                }

            }
        });
    }

    @Override
    public void setMenu(Menu menu) {
        super.setMenu(menu);
        imageDisplay.setMenu(menu);
    }

    private PVReader<Graph2DResult> pv;
    private boolean xRangeModified = false;
    private boolean xRangeEditing = false;
    private boolean yRangeModified = false;
    private boolean yRangeEditing = false;


    Graph2DResult getCurrentResult() {
        if (pv == null) {
            return null;
        } else {
            return pv.getValue();
        }
    }

    public T getGraph() {
        return graph;
    }

    protected U createUpdate() {
        return graph.newUpdate();
    }

    protected void updateGraph() {
        if (getGraph() != null) {
            getGraph().update(createUpdate().xAxisRange(getXAxisRange())
                    .yAxisRange(getYAxisRange()));
        }
    }

    public boolean isResizableAxis() {
        return resizableAxis;
    }

    public void setResizableAxis(boolean resizableAxis) {
        boolean oldValue = this.resizableAxis;
        this.resizableAxis = resizableAxis;
        changeSupport.firePropertyChange("resizableAxis", oldValue, this.resizableAxis);
    }

    private void setLastError(Exception lastException) {
        errorBar.setException(lastException);
    }

    void reconnect() {
        if (pv != null) {
            pv.close();
            imageDisplay.setVImage(null);
            setLastError(null);
            graph = null;
            xRangeControl.resetRange();
            yRangeControl.resetRange();
            processInit();
        }

        if (getDataFormula() == null || getDataFormula().trim().isEmpty()) {
            return;
        }

        graph = createGraph();
        // Allow sub classes to change the parameters of their own graphs
        updateGraph();
        // For views, the reconnect may be triggered before the layout sets
        // the sizes. We make sure no to send an update if the size is 0.
        if (imageDisplay.getSize().x > 0 && imageDisplay.getSize().y > 0) {
            graph.update(graph.newUpdate().imageHeight(imageDisplay.getSize().y)
                    .imageWidth(imageDisplay.getSize().x));
        }
        pv = PVManager.read(graph).notifyOn(SWTUtil.swtThread())
                .readListener(new PVReaderListener<Graph2DResult>() {
                    @Override
                    public void pvChanged(PVReaderEvent<Graph2DResult> event) {
                        setLastError(pv.lastException());
                        if (pv.getValue() != null) {
                            if (!xRangeModified) {
                                xRangeEditing = true;
                                setRange(xRangeControl, pv.getValue().getxRange());
                                xRangeEditing = false;
                            }
                            if (!yRangeModified) {
                                yRangeEditing = true;
                                setRange(yRangeControl, pv.getValue().getyRange());
                                yRangeEditing = false;
                            }
                              imageDisplay.setVImage(pv.getValue().getImage());
                        } else {
                            imageDisplay.setVImage(null);
                        }
                        processValue();
                    }

                }).maxRate(ofHertz(50));
    }

    protected void processInit() {
        // To be extended if needed
    }

    protected void processValue() {
        // To be extended if needed
    }

    protected abstract T createGraph();

    private void setRange(StartEndRangeWidget control, GraphDataRange plotDataRange) {
        if (isResizableAxis() && plotDataRange.getPlotRange() != null && control != null) {
            control.setRange(plotDataRange.getPlotRange().getMinimum()
                    .doubleValue(), plotDataRange.getPlotRange().getMaximum()
                    .doubleValue());
            control.setSelectedRange(plotDataRange.getPlotRange().getMinimum()
                    .doubleValue(), plotDataRange.getPlotRange().getMaximum()
                    .doubleValue());
        }
     }

    private String dataFormula;
    private AxisRange xAxisRange = AxisRanges.display();
    private AxisRange yAxisRange = AxisRanges.display();

    private static final String MEMENTO_DATA_FORMULA = "dataFormula"; //$NON-NLS-1$

    public String getDataFormula() {
        return this.dataFormula;
    }

    public void setDataFormula(String dataFormula) {
        String oldValue = this.dataFormula;
        this.dataFormula = dataFormula;
        changeSupport.firePropertyChange("dataFormula", oldValue,
                this.dataFormula);
    }

    public AxisRange getXAxisRange() {
        return xAxisRange;
    }

    public void setXAxisRange(AxisRange xAxisRange) {
        AxisRange oldValue = this.xAxisRange;
        this.xAxisRange = xAxisRange;
        changeSupport.firePropertyChange("xAxisRange", oldValue,
                this.xAxisRange);
    }

    public AxisRange getYAxisRange() {
        return yAxisRange;
    }

    public void setYAxisRange(AxisRange yAxisRange) {
        AxisRange oldValue = this.yAxisRange;
        this.yAxisRange = yAxisRange;
        changeSupport.firePropertyChange("yAxisRange", oldValue,
                this.yAxisRange);
    }

    public void saveState(IMemento memento) {
        if (getDataFormula() != null) {
            memento.putString(MEMENTO_DATA_FORMULA, getDataFormula());
        }
    }

    public void loadState(IMemento memento) {
        if (memento != null) {
            if (memento.getString(MEMENTO_DATA_FORMULA) != null) {
                setDataFormula(memento.getString(MEMENTO_DATA_FORMULA));
            }
        }
    }

    protected VImageDisplay getImageDisplay() {
        return imageDisplay;
    }

}
