/**
 * 
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vNumberArray;
import static org.epics.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.RangeListener;
import org.csstudio.ui.util.widgets.StartEndRangeWidget;
import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraphRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.LineGraphPlot;
import org.epics.pvmanager.graphene.Plot2DResult;
import org.epics.pvmanager.graphene.PlotDataRange;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotWidget extends Composite implements ISelectionProvider {

    private VImageDisplay imageDisplay;
    private LineGraphPlot plot;
    private ErrorBar errorBar;
    private boolean showRange;
    private StartEndRangeWidget yRangeControl;
    private StartEndRangeWidget xRangeControl;

    // TODO refactor out to some abstract base class
    private ProcessVariable processVariable;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.removePropertyChangeListener(listener);
    }

    public Line2DPlotWidget(Composite parent, int style) {
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
		if (plot != null) {
		    double invert = yRangeControl.getMin()
			    + yRangeControl.getMax();
		    plot.update(new LineGraphRendererUpdate()
			    .rangeFromDataset(false)
			    .startY((invert - yRangeControl.getSelectedMax()))
			    .endY((invert - yRangeControl.getSelectedMin())));
		}
	    }
	});

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
		if (plot != null) {
		    plot.update(new LineGraphRendererUpdate()
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
		if (plot != null) {
		    plot.update(new LineGraphRendererUpdate()
			    .rangeFromDataset(false)
			    .startX(xRangeControl.getSelectedMin())
			    .endX(xRangeControl.getSelectedMax()));
		}
	    }
	});

	this.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println(evt.getPropertyName());
		reconnect();

	    }
	});

    }

    @Override
    public void setMenu(Menu menu) {
	super.setMenu(menu);
	imageDisplay.setMenu(menu);
    }

    private PVReader<Plot2DResult> pv;
    // Y values
    private String yPVName;

    private enum YAxis {
	PVARRAY, WAVEFORM
    }

    private YAxis yOrdering = YAxis.PVARRAY;

    // X values
    private String xPVName;

    private String offset;
    private String increment;

    public enum XAxis {
	INDEX, CHANNELQUERY, OFFSET_INCREMENT
    }

    private XAxis xOrdering = XAxis.INDEX;

    public void setxOrdering(XAxis xAxis) {
	this.xOrdering = xAxis;
	reconnect();
    }

    public XAxis getxOrdering() {
	return xOrdering;
    }

    public String getxPVName() {
	return xPVName;
    }

    public void setxPVName(String xPVName) {
	if (this.xPVName != null && this.xPVName.equals(xPVName)) {
	    return;
	}
	this.xPVName = xPVName;
	reconnect();
    }

    public String getyPVName() {
	return this.yPVName;
    }

    public void setyPVName(String yPVName) {
	if (this.yPVName != null && this.yPVName.equals(yPVName)) {
	    return;
	}
	this.yPVName = yPVName;
	reconnect();
    }

    public String getOffset() {
	return offset;
    }

    public void setOffset(String offset) {
	if (offset != null && !offset.isEmpty()) {
	    this.offset = offset;
	    reconnect();
	}
    }

    public String getIncrement() {
	return increment;
    }

    public void setIncrement(String increment) {
	if (increment != null && !increment.isEmpty()) {
	    this.increment = increment;
	    reconnect();
	}
    }

    private void setLastError(Exception lastException) {
	errorBar.setException(lastException);
    }

    private void reconnect() {
	if (pv != null) {
	    pv.close();
	    imageDisplay.setVImage(null);
	    setLastError(null);
	    plot = null;
	    resetRange(xRangeControl);
	    resetRange(yRangeControl);
	}

	plot = ExpressionLanguage
		.lineGraphOf(latestValueOf(vNumberArray(getxPVName())));
	plot.update(new LineGraphRendererUpdate()
		.imageHeight(imageDisplay.getSize().y)
		.imageWidth(imageDisplay.getSize().x)
		.interpolation(InterpolationScheme.LINEAR));
	pv = PVManager.read(plot).notifyOn(SWTUtil.swtThread())
		.readListener(new PVReaderListener<Plot2DResult>() {
		    @Override
		    public void pvChanged(PVReaderEvent<Plot2DResult> event) {
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

    /**
     * A helper function to set all the appropriate
     * 
     * @param control
     */
    private void setRange(StartEndRangeWidget control,
	    PlotDataRange plotDataRange) {
	control.setRange(plotDataRange.getStartIntegratedDataRange(),
		plotDataRange.getEndIntegratedDataRange());
    }

    private void resetRange(StartEndRangeWidget control) {
	control.setRanges(0, 0, 1, 1);
    }

    private Map<ISelectionChangedListener, PropertyChangeListener> listenerMap = new HashMap<ISelectionChangedListener, PropertyChangeListener>();

    @Override
    public void addSelectionChangedListener(
	    final ISelectionChangedListener listener) {
	PropertyChangeListener propListener = new PropertyChangeListener() {
	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
		if ("processVariable".equals(event.getPropertyName()))
		    listener.selectionChanged(new SelectionChangedEvent(
			    Line2DPlotWidget.this, getSelection()));
	    }
	};
	listenerMap.put(listener, propListener);
	addPropertyChangeListener(propListener);
    }

    @Override
    public ISelection getSelection() {
	return new StructuredSelection(getxPVName());
    }

    @Override
    public void removeSelectionChangedListener(
	    ISelectionChangedListener listener) {
	removePropertyChangeListener(listenerMap.remove(listener));
    }

    @Override
    public void setSelection(ISelection selection) {
	throw new UnsupportedOperationException("Not implemented yet");
    }

    /** Memento tag */
    private static final String MEMENTO_XPVNAME = "XPVName"; //$NON-NLS-1$
    private static final String MEMENTO_YPVNAME = "YPVName"; //$NON-NLS-1$

    public void saveState(IMemento memento) {
	if (getxPVName() != null) {
	    memento.putString(MEMENTO_XPVNAME, getxPVName());	    
	}
	if (getyPVName() != null) {
	    memento.putString(MEMENTO_YPVNAME, getyPVName());	    
	}	
    }

    public void loadState(IMemento memento) {
	if (memento != null) {
	    if (memento.getString(MEMENTO_XPVNAME) != null) {
		setxPVName(memento.getString(MEMENTO_XPVNAME));
	    }
	    if (memento.getString(MEMENTO_YPVNAME) != null) {
		setyPVName(memento.getString(MEMENTO_YPVNAME));
	    }
	}
    }
}
