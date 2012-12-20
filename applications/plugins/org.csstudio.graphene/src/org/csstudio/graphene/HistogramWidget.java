package org.csstudio.graphene;

import static org.epics.pvmanager.data.ExpressionLanguage.*;
import static org.epics.pvmanager.graphene.ExpressionLanguage.*;
import static org.epics.pvmanager.util.TimeDuration.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.epics.graphene.Histogram1DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.graphene.Histogram1DPlot;

public class HistogramWidget extends Composite {
	
	private VImageDisplay imageDisplay;
	private Histogram1DPlot plot;
	private ErrorBar errorBar;
	private boolean editable = true;

	/**
	 * Creates a new widget.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public HistogramWidget(Composite parent, int style) {
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
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		imageDisplay = new VImageDisplay(this);
		imageDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		imageDisplay.setStretched(SWT.HORIZONTAL | SWT.VERTICAL);
		imageDisplay.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				changePlotSize(imageDisplay.getSize().x, imageDisplay.getSize().y);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// Nothing to do
			}
		});
		
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("processVariable".equals(event.getPropertyName())) {
					reconnect();
				}
			}
		});
		
	}
	
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		imageDisplay.setMenu(menu);
	}
	
	// The pv name for waveform
	private String waveformPVName;
	// The pv names for multiple channels
	private List<String> scalarPVNames;
	// The pv created by pvmanager
	private PVReader<VImage> pv;
	
	/**
	 * The pv name to connect to.
	 * 
	 * @return the current property value
	 */
	public String getWaveformPVName() {
		return waveformPVName;
	}
	
	/**
	 * Changes the pv name to connect to. Triggers a reconnection.
	 * 
	 * @param pvName the new property value
	 */
	public void setWaveformPVName(String pvName) {
		// Guard from double calls
		if (this.waveformPVName != null && this.waveformPVName.equals(pvName)) {
			return;
		}
		
		this.scalarPVNames = null;
		this.waveformPVName = pvName;
		reconnect();
	}
	
	public List<String> getScalarPVNames() {
		return scalarPVNames;
	}
	
	public void setScalarPVNames(List<String> scalarPVNames) {
		// Guard from double calls
		if (this.scalarPVNames != null && this.scalarPVNames.equals(scalarPVNames)) {
			return;
		}
		
		this.waveformPVName = null;
		this.scalarPVNames = scalarPVNames;
		reconnect();
	}
	
	/**
	 * Whether the user is able to customize the widget.
	 * 
	 * @return true if it can be customized
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * Changes whether the user is able to customize the widget.
	 * 
	 * @param editable true if it can be customized
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	// Displays the last error generated
	private void setLastError(Exception ex) {
		errorBar.setException(ex);
	}
	
	// Reconnects the pv
	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}
		
		imageDisplay.setVImage(null);
		
		if (getProcessVariable() == null || getProcessVariable().getName().trim().isEmpty()) {
			return;
		}
		
		plot = histogramOf(vDouble(getProcessVariable().getName()));
		plot.update(new Histogram1DRendererUpdate()
				.imageWidth(imageDisplay.getSize().x).imageHeight(imageDisplay.getSize().y));
		pv = PVManager.read(plot).notifyOn(SWTUtil.swtThread()).every(hz(50));
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				setLastError(pv.lastException());
				imageDisplay.setVImage(pv.getValue());
			}
		});
	}
	
	private void changePlotSize(int newWidgth, int newHeight) {
		if (plot != null) {
			plot.update(new Histogram1DRendererUpdate()
			.imageHeight(newHeight).imageWidth(newWidgth));
		}
	}
	
	private ProcessVariable processVariable;
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
	
	public ProcessVariable getProcessVariable() {
		return processVariable;
	}
	
	public void setProcessVariable(ProcessVariable processVariable) {
		ProcessVariable oldValue = this.processVariable;
		this.processVariable = processVariable;
		changeSupport.firePropertyChange("processVariable", oldValue, processVariable);
	}

}
