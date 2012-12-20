package org.csstudio.graphene;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;
import static org.epics.pvmanager.graphene.ExpressionLanguage.lineGraphOf;
import static org.epics.pvmanager.util.TimeDuration.hz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraphRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.graphene.LineGraphPlot;
import org.epics.pvmanager.graphene.Plot2DResult;

public class LineGraphWidget extends Composite {
	
	private VImageDisplay imageDisplay;
	private LineGraphPlot plot;
	private ErrorBar errorBar;
	private boolean editable = true;

	/**
	 * Creates a new widget.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public LineGraphWidget(Composite parent, int style) {
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
	
	// The pv created by pvmanager
	private PVReader<Plot2DResult> pv;
	
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
		
		plot = lineGraphOf(latestValueOf(vNumberArray(getProcessVariable().getName())));
		plot.update(new LineGraphRendererUpdate()
				.imageWidth(imageDisplay.getSize().x).imageHeight(imageDisplay.getSize().y)
				.interpolation(InterpolationScheme.LINEAR));
		pv = PVManager.read(plot).notifyOn(SWTUtil.swtThread()).every(hz(50));
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				setLastError(pv.lastException());
				if (pv.getValue() != null) {
					imageDisplay.setVImage(pv.getValue().getImage());
				} else {
					imageDisplay.setVImage(null);
				}
			}
		});
	}
	
	private void changePlotSize(int newWidgth, int newHeight) {
		if (plot != null) {
			plot.update(new LineGraphRendererUpdate()
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
