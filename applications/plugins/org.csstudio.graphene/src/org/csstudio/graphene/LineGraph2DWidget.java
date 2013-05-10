/**
 * 
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;
import static org.epics.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.RangeListener;
import org.csstudio.ui.util.widgets.StartEndRangeWidget;
import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.epics.graphene.AxisRanges;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.LineGraph2DExpression;
import org.epics.vtype.VNumberArray;

/**
 * A simple Line 2D plot which can handle both waveforms and a list of PVs
 * 
 * @author shroffk
 * 
 */
public class LineGraph2DWidget extends AbstractPointDatasetGraph2DWidget implements
	 ConfigurableWidget {

    private VImageDisplay imageDisplay;
    private LineGraph2DExpression graph;
    private ErrorBar errorBar;
    private boolean showAxis = true;
    private StartEndRangeWidget yRangeControl;
    private StartEndRangeWidget xRangeControl;

    public LineGraph2DWidget(Composite parent, int style) {
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
		if (graph != null) {
		    double invert = yRangeControl.getMin()
			    + yRangeControl.getMax();
		    graph.update(new LineGraph2DRendererUpdate()
			    .yAxisRange(AxisRanges.absolute(invert
				    - yRangeControl.getSelectedMax(), invert
				    - yRangeControl.getSelectedMin())));
		}
	    }
	});
	yRangeControl.setVisible(showAxis);

	imageDisplay = new VImageDisplay(this);
	FormData fd_imageDisplay = new FormData();
	fd_imageDisplay.top = new FormAttachment(errorBar, 2);
	fd_imageDisplay.right = new FormAttachment(100, -2);
	fd_imageDisplay.left = new FormAttachment(yRangeControl, 2);
	imageDisplay.setLayoutData(fd_imageDisplay);
	imageDisplay.setStretched(SWT.HORIZONTAL);

	imageDisplay.addControlListener(new ControlListener() {

	    @Override
	    public void controlResized(ControlEvent e) {
		if (graph != null) {
		    graph.update(new LineGraph2DRendererUpdate()
			    .imageHeight(imageDisplay.getSize().y)
			    .imageWidth(imageDisplay.getSize().x)
			    .interpolation(InterpolationScheme.LINEAR));
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
		if (graph != null) {
		    graph.update(new LineGraph2DRendererUpdate()
			    .xAxisRange(AxisRanges.absolute(
				    xRangeControl.getSelectedMin(),
				    xRangeControl.getSelectedMax())));
		}
	    }
	});
	xRangeControl.setVisible(showAxis);

	addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("dataFormula")
					|| event.getPropertyName().equals("xColumnFormula")
					|| event.getPropertyName().equals("yColumnFormula")
					|| event.getPropertyName().equals("tooltipFormula")) {
				reconnect();
		} else if (event.getPropertyName().equals("showAxis")) {
		    xRangeControl.setVisible(showAxis);
		    yRangeControl.setVisible(showAxis);
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

    public boolean isShowAxis() {
	return showAxis;
    }

    public void setShowAxis(boolean showAxis) {
	boolean oldValue = this.showAxis;
	this.showAxis = showAxis;
	changeSupport.firePropertyChange("showAxis", oldValue, this.showAxis);
    }

    private void setLastError(Exception lastException) {
	errorBar.setException(lastException);
    }

    @Override
    @SuppressWarnings("unchecked")
    void reconnect() {
	if (pv != null) {
	    pv.close();
	    imageDisplay.setVImage(null);
	    setLastError(null);
	    graph = null;
	    resetRange(xRangeControl);
	    resetRange(yRangeControl);
	}
	graph = ExpressionLanguage.lineGraphOf(formula(getDataFormula()),
			formulaArg(getXColumnFormula()),
			formulaArg(getYColumnFormula()),
			formulaArg(getTooltipFormula()));
	graph.update(graph.newUpdate()
	        .imageHeight(imageDisplay.getSize().y)
	        .imageWidth(imageDisplay.getSize().x)
			.interpolation(InterpolationScheme.LINEAR));
	pv = PVManager.read(graph).notifyOn(SWTUtil.swtThread())
		.readListener(new PVReaderListener<Graph2DResult>() {
		    @Override
		    public void pvChanged(PVReaderEvent<Graph2DResult> event) {
			Exception ex = pv.lastException();

			if (ex != null) {
			    setLastError(ex);
			}
			if (pv.getValue() != null) {
			    setRange(xRangeControl, pv.getValue().getxRange());
			    setRange(yRangeControl, pv.getValue().getyRange());
			    imageDisplay.setVImage(pv.getValue().getImage());
			} else {
			    imageDisplay.setVImage(null);
			}
		    }
		}).maxRate(ofHertz(50));
    }

    /** Memento tag */
    private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$
//
//    public void saveState(IMemento memento) {
//	if (getPvName() != null) {
//	    memento.putString(MEMENTO_PVNAME, getPvName());
//	}
//    }
//
//    public void loadState(IMemento memento) {
//	if (memento != null) {
//	    if (memento.getString(MEMENTO_PVNAME) != null) {
//		setPvName(memento.getString(MEMENTO_PVNAME));
//	    }
//	}
//    }

//    @Override
//    public ISelection getSelection() {
//	if (getPvName() != null) {
//	    return new StructuredSelection(new LineGraph2DSelection(
//		    new ProcessVariable(getPvName()),
//		    getXpvName() != null ? new ProcessVariable(getXpvName())
//			    : null, this));
//	}
//	return null;
//    }
//
//    @Override
//    public void addSelectionChangedListener(
//	    final ISelectionChangedListener listener) {
//    }
//
//    @Override
//    public void removeSelectionChangedListener(
//	    ISelectionChangedListener listener) {
//    }
//
//    @Override
//    public void setSelection(ISelection selection) {
//	throw new UnsupportedOperationException("Not implemented yet");
//    }

    private boolean configurable = true;

    private Graph2DConfigurationDialog dialog;

    @Override
    public boolean isConfigurable() {
	return this.configurable;
    }

    @Override
    public void setConfigurable(boolean configurable) {
	boolean oldValue = this.configurable;
	this.configurable = configurable;
	changeSupport.firePropertyChange("configurable", oldValue,
		this.configurable);
    }

    @Override
    public void openConfigurationDialog() {
	if (dialog != null)
	    return;
	dialog = new Graph2DConfigurationDialog(this, "Configure Line Graph");
	dialog.open();
    }

    @Override
    public boolean isConfigurationDialogOpen() {
	return dialog != null;
    }

    @Override
    public void configurationDialogClosed() {
	dialog = null;
    }
}
