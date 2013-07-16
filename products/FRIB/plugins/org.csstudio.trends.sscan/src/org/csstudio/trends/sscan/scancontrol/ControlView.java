package org.csstudio.trends.sscan.scancontrol;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.hz;
import static org.epics.pvmanager.util.TimeDuration.ms;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.TimeoutException;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.util.time.TimeDuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlView extends Composite {
	
	private static final Logger log = Logger.getLogger(ControlView.class.getName());
	
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	/** Currently displayed pv */
	private ProcessVariable PVName;
	private ProcessVariable oldPvName;
	
    /** Listeners to model changes */
    final private ArrayList<SscanListener> listeners = new ArrayList<SscanListener>();

	/** Currently connected pv */
	private PV<Map<String, Object>, Map<String, Object>> pv;
	private PVReader<Map<String, Object>> pvCpt;
	
	private ComboViewer sscanRecord;
	private ComboHistoryHelper pvNameHelper;
	private Label pVConnectionStatus;
	private ProgressBar progressBar;
	private Button execScan;
	
	/** Formatting used for the value text field */
	private ValueFormat valueFormat = new SimpleValueFormat(3);
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ControlView(Composite parent, int style, String SSCAN_PV_TAG, String PV_LIST_TAG) {
		super(parent, style);
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 23, 257, 2);
		
		Label lblScanControl = new Label(this, SWT.NONE);
		lblScanControl.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		lblScanControl.setBounds(0, 0, 257, 25);
		lblScanControl.setText("Scan Control");
		
		progressBar = new ProgressBar(this, SWT.NONE);
		progressBar.setBounds(10, 67, 237, 18);
		
		execScan = new Button(this, SWT.NONE);
		execScan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(PVName != null) {
					Map<String, Object> values = new HashMap<String, Object>();
					VInt execSscan = (VInt)pv.getValue().get("execSscan");
					if((int)execSscan.getValue()==1){
						values.put("execSscan", 0);
					} else {
						values.put("execSscan", 1);
					}
					pv.write(values);
				}
			}
		});
		execScan.setBounds(10, 91, 237, 23);
		execScan.setText("Scan");
		
		sscanRecord = new ComboViewer(this, SWT.NONE | SWT.BORDER);
		sscanRecord.getCombo().setBounds(10, 40, 237, 21);
		sscanRecord.getCombo().setToolTipText(Messages.Scan_pvNameFieldToolTipText);
		
		// Connect actions
		pvNameHelper = new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), PV_LIST_TAG, sscanRecord.getCombo()) {
			@Override
			public void newSelection(final String pvName) {
				setPVName(new ProcessVariable(pvName));
			}
		};
		
		pVConnectionStatus = new Label(this, SWT.NONE);
		pVConnectionStatus.setText(Messages.Scan_statusWaitingForPV);
		pVConnectionStatus.setBounds(10, 118, 237, 13);
		
		sscanRecord.getCombo().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				if(pv != null)
					pv.close();
				if(pvCpt!=null)
				    pvCpt.close();
				pvNameHelper.saveSettings();
			}
		});

	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Changes the PV currently displayed by control composite.
	 * 
	 * @param pvName
	 *            the new pv name or null
	 */
	public void setPVName(ProcessVariable pvName) {
		log.log(Level.FINE, "setPVName ({0})", pvName); //$NON-NLS-1$

		// If we are already scanning that pv, do nothing
		if (this.PVName != null && this.PVName.equals(pvName)) {
			// XXX Seems like something is clearing the combo-box,
			// reset to the actual pv...
			sscanRecord.getCombo().setText(pvName.getName());
		}

		// The PV is different, so disconnect and reset the visuals
		if (pv != null) {
			pv.close();
			pvCpt.close();
			pvCpt = null;
			pv = null;
		}

		// If name is blank, update status to waiting and quit
		if ((pvName.getName() == null) || pvName.getName().trim().isEmpty()) {
			sscanRecord.getCombo().setText(""); //$NON-NLS-1$
			setStatus(Messages.Scan_statusWaitingForPV);
		}

		// If new name, add to history and connect
		pvNameHelper.addEntry(pvName.getName());

		// Update displayed name, unless it's already current
		if (!(sscanRecord.getCombo().getText().equals(pvName
				.getName()))) {
			sscanRecord.getCombo().setText(pvName.getName());
		}

		setStatus(Messages.Scan_statusSearching);
		
		pv = PVManager.readAndWrite(mapOf(latestValueOf(channel(pvName.getName()+".EXSC").as("execSscan"))))
				.timeout(ms(5000), "No connection after 5s. Still trying...")
				.notifyOn(swtThread())
				.asynchWriteAndReadEvery(hz(25));
		
		pvCpt = PVManager.read(mapOf(latestValueOf(channel(pvName.getName()+".CPT").as("currentPoint")
				.and(channel(pvName.getName()+".NPTS").as("numberPoints")))))
				.notifyOn(swtThread())
				.every(hz(25));
		
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Map<String, Object> map = pv.getValue();
				VInt execSscan = (VInt)map.get("execSscan");
					
				setLastError(pv.lastException());
				setScanAbort(execSscan.getValue());
				fireScanEventChanged(PVName.getName(),execSscan.getValue() );

			}
		});
		
		pvCpt.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Map<String, Object> map = pvCpt.getValue();
				VInt currentPoint = (VInt)map.get("currentPoint");
				VInt numberPoints = (VInt)map.get("numberPoints");
				
				setProgressBar((int)(((double)currentPoint.getValue()/(double)numberPoints.getValue())*100));
				setLastError(pvCpt.lastException());
			}
		});
		oldPvName = this.PVName;
		this.PVName = pvName;
		changeSupport.firePropertyChange("channel", oldPvName, pvName);
		firePVNameChanged(PVName.getName());
		firePVNameChanged(PVName);
		
		// If this is an instance of the multiple view, show the PV name
		// as the title
		//if (MULTIPLE_VIEW_ID.equals(getSite().getId())) {
		//	setPartName(pvName.getName());
		//}
	}

	/**
	 * Returns the currently displayed PV.
	 * 
	 * @return pv name or null
	 */
	public ProcessVariable getPVName() {
		return this.PVName;
	}
	
	/**
	 * Modifies the status.
	 * 
	 * @param status new status to be displayed
	 */
	private void setStatus(String status) {
		if (status == null) {
			pVConnectionStatus.setText(Messages.Scan_statusWaitingForPV);
		} else {
			pVConnectionStatus.setText(status);
		}
	}
	
	/**
	 * Modifies the progress bar.
	 * 
	 * @param status new progress bar status to be displayed
	 */
	private void setScanAbort(int value) {
		if (value == 0) {
			execScan.setText("Scan");
		} else {
			execScan.setText("Abort");
		}
	}
	
	/**
	 * Modifies the progress bar.
	 * 
	 * @param status new progress bar status to be displayed
	 */
	private void setProgressBar(int value) {
		if (Integer.valueOf(value) == null) {
			progressBar.setSelection(0);
		} else {
			progressBar.setSelection(value);
		}
	}
	
	/**
	 * Displays the last error in the status.
	 * 
	 * @param ex an exception
	 */
	private void setLastError(Exception ex) {
		if (ex == null) {
			// If no exception, then everything is peachy
			pVConnectionStatus.setText(Messages.Scan_statusConnected);
		} else if (!(ex instanceof TimeoutException) || Messages.Scan_statusSearching.equals(pVConnectionStatus.getText())) {
			// If it's an error always display message, but if it's
			// a timeout display only if there was no previous message
			pVConnectionStatus.setText(ex.getMessage());
		}
	}
	
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
    
	/** @param listener New listener to notify */
    public void addListener(final SscanListener listener)
    {
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final SscanListener listener)
    {
        listeners.remove(listener);
    }
    
    /** Notify listeners of changed item data
     *  @param item Item that changed
     */
    void firePVNameChanged(final String PVName)
    {
        for (SscanListener listener : listeners)
            listener.PVName(PVName);
    }
    
    /** Notify listeners of changed item data
     *  @param item Item that changed
     */
    void firePVNameChanged(final ProcessVariable PVName)
    {
        for (SscanListener listener : listeners)
            listener.PVName(PVName);
    }
    
    /** Notify listeners of changed item data
     *  @param item Item that changed
     */
    void fireScanEventChanged(final String name, final int status)
    {
        for (SscanListener listener : listeners)
            listener.ScanEvent(name, status);
    }
    
}
