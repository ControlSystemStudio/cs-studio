package org.csstudio.trends.sscan.scancontrol;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.hz;
import static org.epics.pvmanager.util.TimeDuration.ms;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.wb.swt.SWTResourceManager;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.Enum;
import org.epics.pvmanager.data.ValueUtil;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PropertiesView extends Composite {
	private Text numPoints;
	private Text positionerDelay;
	private Text detectorDelay;
	private Combo positionerAfterScan;
    
	private ProcessVariable PVName;
	private PVReader<Map<String, Object>> pv;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PropertiesView(Composite parent, int style, String SSCAN_PV_TAG) {
		super(parent, style);
		
		numPoints = new Text(this, SWT.BORDER);
		numPoints.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".NPTS")).async();		 
				pvWriter.write(numPoints.getText());
				pvWriter.close();
			}
		});
		numPoints.setBounds(154, 33, 76, 19);
		
		positionerDelay = new Text(this, SWT.BORDER);
		positionerDelay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".PDLY")).async();		 
				pvWriter.write(positionerDelay.getText());
				pvWriter.close();
			}
		});
		positionerDelay.setBounds(154, 58, 92, 19);
		
		detectorDelay = new Text(this, SWT.BORDER);
		detectorDelay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".DDLY")).async();		 
				pvWriter.write(detectorDelay.getText());
				pvWriter.close();
			}
		});
		detectorDelay.setBounds(154, 83, 92, 19);
		
		positionerAfterScan = new Combo(this, SWT.NONE);
		positionerAfterScan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".PASM")).async();		 
				pvWriter.write(positionerAfterScan.getText());
				pvWriter.close();
			}
		});
		positionerAfterScan.setBounds(154, 108, 92, 21);
		
		Label lblNumPoints = new Label(this, SWT.NONE);
		lblNumPoints.setAlignment(SWT.RIGHT);
		lblNumPoints.setBounds(56, 39, 92, 13);
		lblNumPoints.setText("Number of Points");
		
		Label lblPositionerDelay = new Label(this, SWT.NONE);
		lblPositionerDelay.setAlignment(SWT.RIGHT);
		lblPositionerDelay.setText("Positioner Delay");
		lblPositionerDelay.setBounds(56, 64, 92, 13);
		
		Label lblDetectorDelay = new Label(this, SWT.NONE);
		lblDetectorDelay.setAlignment(SWT.RIGHT);
		lblDetectorDelay.setText("Detector Delay");
		lblDetectorDelay.setBounds(56, 89, 92, 13);
		
		Label lblPositionerAfterScan = new Label(this, SWT.NONE);
		lblPositionerAfterScan.setAlignment(SWT.RIGHT);
		lblPositionerAfterScan.setText("Positioner After Scan");
		lblPositionerAfterScan.setBounds(35, 116, 113, 13);
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 20, 256, 2);
		
		Label lblScanProperties = new Label(this, SWT.NONE);
		lblScanProperties.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		lblScanProperties.setBounds(0, 0, 256, 22);
		lblScanProperties.setText("Scan Properties");

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
		
		pv = PVManager.read(mapOf(latestValueOf(channel(pvName.getName()+".PDLY").as("positionerDelay")
				.and(channel(pvName.getName()+".DDLY").as("detectorDelay"))
				.and(channel(pvName.getName()+".PASM").as("positionerAfterScan"))
				.and(channel(pvName.getName()+".NPTS").as("numberPoints")))))
				.notifyOn(swtThread())
				.every(hz(25));

		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Map<String, Object> map = pv.getValue();
				VInt numberPoints = (VInt)map.get("numberPoints");
				VDouble pDelay = (VDouble)map.get("positionerDelay");
				VDouble dDelay = (VDouble)map.get("detectorDelay");
				VEnum pAfterScan = (VEnum)map.get("positionerAfterScan");
				
				//TODO: After Scan Options should happen once?
				if(pAfterScan!=null){
					setpAfterScanOptions(pAfterScan.getLabels());
					setpAfterScan(pAfterScan.getIndex());
				}
				if(numberPoints!=null)
					setNumPoints(numberPoints.getValue().toString());
				if(pDelay!=null)
					setPositionerDelay(pDelay.getValue().toString());
				if(dDelay!=null)
					setDetectorDelay(dDelay.getValue().toString());
			}
		});
		
		this.PVName=pvName;
	}
	
	/**
	 * Modifies the positioner after scan combo
	 * 
	 * @param value new value to be displayed
	 */
	private void setpAfterScan(int value) {
		positionerAfterScan.select(value);
	}
	
	/**
	 * Modifies the positioner after scan combo Options
	 * 
	 * @param list new options to be displayed
	 */
	private void setpAfterScanOptions(List<String> list) {
		positionerAfterScan.setItems(list.toArray(new String[list.size()]));
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
	 * Modifies the Number of points
	 * 
	 * @param value new value to be displayed
	 */
	private void setNumPoints(String status) {
		if (status == null) {
			numPoints.setText("0");
		} else {
			numPoints.setText(status);
		}
	}
	
	/**
	 * Modifies the Positioner Delay
	 * 
	 * @param value new value to be displayed
	 */
	private void setPositionerDelay(String status) {
		if (status == null) {
			positionerDelay.setText("0");
		} else {
			positionerDelay.setText(status);
		}
	}
	
	/**
	 * Modifies the Detector Delay
	 * 
	 * @param value new value to be displayed
	 */
	private void setDetectorDelay(String status) {
		if (status == null) {
			detectorDelay.setText("0");
		} else {
			detectorDelay.setText(status);
		}
	}
}
