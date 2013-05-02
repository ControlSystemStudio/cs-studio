package org.csstudio.trends.sscan.scancontrol;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.hz;

import java.util.Arrays;
import java.util.Map;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.ValueUtil;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PositionerView extends Composite {
	private Text positionerPV;
	private Text positionerStart;
	private Text positionerCenter;
	private Text positionerEnd;
	private Text positionerStepSize;
	private Text positionerWidth;
	private Combo positionerNum;
	private Label positionerUnitsReadback;
	private Label positionerReadback;
	private Integer positionerIndex;
	private Text readPositionerPV;
	
	private ProcessVariable PVName;
	private PVReader<Map<String, Object>> pv;

	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PositionerView(Composite parent, int style, String SSCAN_PV_TAG) {
		super(parent, style);
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 23, 257, 2);
		
		positionerNum = new Combo(this, SWT.NONE);
		positionerNum.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				positionerIndex = positionerNum.getSelectionIndex()+1;
				setPVName(PVName);
			}
		});
		positionerNum.setItems(Arrays.asList("Positioner 1","Positioner 2","Positioner 3","Positioner 4").toArray(new String[4]));
		positionerNum.select(0);
		positionerNum.setBounds(155, 32, 92, 21);
		
		positionerPV = new Text(this, SWT.BORDER);
		positionerPV.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".P"+positionerIndex.toString()+"PV")).async();		 
				pvWriter.write(positionerPV.getText());
				pvWriter.close();
			}
		});
		positionerPV.setBounds(78, 59, 169, 19);
		
		readPositionerPV = new Text(this, SWT.BORDER);
		readPositionerPV.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".R"+positionerIndex.toString()+"PV")).async();		 
				pvWriter.write(readPositionerPV.getText());
				pvWriter.close();
			}
		});
		readPositionerPV.setMessage("(if different from set)");
		readPositionerPV.setBounds(78, 84, 169, 19);
		
		positionerStart = new Text(this, SWT.BORDER);
		positionerStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".P"+positionerIndex.toString()+"SP")).async();		 
				pvWriter.write(positionerStart.getText());
				pvWriter.close();
			}
		});
		positionerStart.setBounds(155, 144, 92, 19);
		
		positionerCenter = new Text(this, SWT.BORDER);
		positionerCenter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".P"+positionerIndex.toString()+"CP")).async();		 
				pvWriter.write(positionerCenter.getText());
				pvWriter.close();
			}
		});
		positionerCenter.setBounds(155, 169, 92, 19);
		
		positionerEnd = new Text(this, SWT.BORDER);
		positionerEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".P"+positionerIndex.toString()+"EP")).async();		 
				pvWriter.write(positionerEnd.getText());
				pvWriter.close();
			}
		});
		positionerEnd.setBounds(155, 194, 92, 19);
		
		positionerStepSize = new Text(this, SWT.BORDER);
		positionerStepSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".P"+positionerIndex.toString()+"SI")).async();		 
				pvWriter.write(positionerStepSize.getText());
				pvWriter.close();
			}
		});
		positionerStepSize.setBounds(155, 219, 92, 19);
		
		positionerWidth = new Text(this, SWT.BORDER);
		positionerWidth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".P"+positionerIndex.toString()+"WD")).async();		 
				pvWriter.write(positionerWidth.getText());
				pvWriter.close();
			}
		});
		positionerWidth.setBounds(155, 244, 92, 19);
		
		Label lblPositioners = new Label(this, SWT.NONE);
		lblPositioners.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		lblPositioners.setBounds(0, 0, 257, 25);
		lblPositioners.setText("Positioners");
		
		Label lblPositioner = new Label(this, SWT.NONE);
		lblPositioner.setAlignment(SWT.RIGHT);
		lblPositioner.setBounds(57, 35, 92, 13);
		lblPositioner.setText("Positioner");
		
		Label lblDevice = new Label(this, SWT.NONE);
		lblDevice.setText("Set Device");
		lblDevice.setAlignment(SWT.RIGHT);
		lblDevice.setBounds(10, 62, 61, 13);
		
		Label lblReading = new Label(this, SWT.NONE);
		lblReading.setText("Reading");
		lblReading.setAlignment(SWT.RIGHT);
		lblReading.setBounds(57, 125, 92, 13);
		
		Label lblStartPoint = new Label(this, SWT.NONE);
		lblStartPoint.setText("Start");
		lblStartPoint.setAlignment(SWT.RIGHT);
		lblStartPoint.setBounds(57, 150, 92, 13);
		
		Label lblEndPoint = new Label(this, SWT.NONE);
		lblEndPoint.setText("Center");
		lblEndPoint.setAlignment(SWT.RIGHT);
		lblEndPoint.setBounds(57, 175, 92, 13);
		
		Label lblEnd = new Label(this, SWT.NONE);
		lblEnd.setText("End");
		lblEnd.setAlignment(SWT.RIGHT);
		lblEnd.setBounds(57, 200, 92, 13);
		
		Label lblStepSize = new Label(this, SWT.NONE);
		lblStepSize.setText("Step Size");
		lblStepSize.setAlignment(SWT.RIGHT);
		lblStepSize.setBounds(57, 225, 92, 13);
		
		Label lblWidth = new Label(this, SWT.NONE);
		lblWidth.setText("Width");
		lblWidth.setAlignment(SWT.RIGHT);
		lblWidth.setBounds(57, 250, 92, 13);
		
		positionerReadback = new Label(this, SWT.BORDER);
		positionerReadback.setBounds(155, 119, 49, 19);
		
		positionerUnitsReadback = new Label(this, SWT.NONE);
		positionerUnitsReadback.setBounds(210, 119, 37, 19);
		
		Label lblReadDevice = new Label(this, SWT.NONE);
		lblReadDevice.setText("Read Device");
		lblReadDevice.setAlignment(SWT.RIGHT);
		lblReadDevice.setBounds(10, 90, 61, 13);

	}
	
	/**
	 * Changes the PV currently displayed by control composite.
	 * 
	 * @param pvName
	 *            the new pv name or null
	 */
	public void setPVName(ProcessVariable pvName) {
		if(positionerIndex==null)
			positionerIndex=1;
		pv = PVManager.read(mapOf(latestValueOf(channel(pvName.getName()+".P"+positionerIndex.toString()+"PV").as("positionerPV")
				.and(channel(pvName.getName()+".P"+positionerIndex.toString()+"SP").as("startPoint"))
				.and(channel(pvName.getName()+".P"+positionerIndex.toString()+"EP").as("endPoint"))
				.and(channel(pvName.getName()+".P"+positionerIndex.toString()+"CP").as("centerPoint"))
				.and(channel(pvName.getName()+".P"+positionerIndex.toString()+"WD").as("positionerWidth"))
				.and(channel(pvName.getName()+".P"+positionerIndex.toString()+"SI").as("positionerStepIncrement"))
				.and(channel(pvName.getName()+".R"+positionerIndex.toString()+"PV").as("readPositionerPV"))
				.and(channel(pvName.getName()+".R"+positionerIndex.toString()+"CV").as("readPositioner"))
				.and(channel(pvName.getName()+".P"+positionerIndex.toString()+"EU").as("engUnits")))))
				.notifyOn(swtThread())
				.every(hz(25));

		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Map<String, Object> map = pv.getValue();
				VString pPV = (VString)map.get("positionerPV");
				VString pReadPV = (VString)map.get("readPositionerPV");
				Object pRead = map.get("readPositioner");
				Object pStart = map.get("startPoint");
				Object pEnd = map.get("endPoint");
				Object pCenter = map.get("centerPoint");
				Object pWidth = map.get("positionerWidth");
				Object pStepSize = map.get("positionerStepIncrement");
				VString pUnit = (VString)map.get("engUnits");
				
				if(pPV!=null)
					setpPV(pPV.getValue());
				if(pReadPV!=null)
					setpReadPV(pReadPV.getValue());
				if(pRead!=null)
					setpRead(ValueUtil.numericValueOf(pRead));
				if(pStart!=null)
					setpStart(ValueUtil.numericValueOf(pStart));
				if(pEnd!=null)
					setpEnd(ValueUtil.numericValueOf(pEnd));
				if(pCenter!=null)
					setpCenter(ValueUtil.numericValueOf(pCenter));
				if(pWidth!=null)
					setpWidth(ValueUtil.numericValueOf(pWidth));
				if(pStepSize!=null)
					setpStepSize(ValueUtil.numericValueOf(pStepSize));
				if(pUnit!=null)
					setpUnit(pUnit.getValue());
			}
		});
		
		this.PVName=pvName;
	}
	
	/**
	 * Modifies the Positioner PV
	 * 
	 * @param value new value to be displayed
	 */
	private void setpPV(String value) {
		if (value == null) {
			positionerPV.setText("");
		} else {
			positionerPV.setText(value);
		}
	}
	
	/**
	 * Modifies the Read Positioner PV
	 * 
	 * @param value new value to be displayed
	 */
	private void setpReadPV(String value) {
		if (value == null) {
			readPositionerPV.setText("");
		} else {
			readPositionerPV.setText(value);
		}
	}
	
	/**
	 * Modifies the Read Positioner
	 * 
	 * @param value new value to be displayed
	 */
	private void setpRead(Double value) {
		if (value == null) {
			positionerReadback.setText("");
		} else {
			positionerReadback.setText(value.toString());
		}
	}
	
	/**
	 * Modifies the Positioner start
	 * 
	 * @param value new value to be displayed
	 */
	private void setpStart(Double value) {
		if (value == null) {
			positionerStart.setText("");
		} else {
			positionerStart.setText(value.toString());
		}
	}
	
	/**
	 * Modifies the Positioner end
	 * 
	 * @param value new value to be displayed
	 */
	private void setpEnd(Double value) {
		if (value == null) {
			positionerEnd.setText("");
		} else {
			positionerEnd.setText(value.toString());
		}
	}
	
	/**
	 * Modifies the Positioner center
	 * 
	 * @param value new value to be displayed
	 */
	private void setpCenter(Double value) {
		if (value == null) {
			positionerCenter.setText("");
		} else {
			positionerCenter.setText(value.toString());
		}
	}
	
	/**
	 * Modifies the Positioner width
	 * 
	 * @param value new value to be displayed
	 */
	private void setpWidth(Double value) {
		if (value == null) {
			positionerWidth.setText("");
		} else {
			positionerWidth.setText(value.toString());
		}
	}
	
	/**
	 * Modifies the Positioner step size
	 * 
	 * @param value new value to be displayed
	 */
	private void setpStepSize(Double value) {
		if (value == null) {
			positionerStepSize.setText("");
		} else {
			positionerStepSize.setText(value.toString());
		}
	}
	
	/**
	 * Modifies the Positioner PV
	 * 
	 * @param value new value to be displayed
	 */
	private void setpUnit(String value) {
		if (value == null) {
			positionerUnitsReadback.setText("");
		} else {
			positionerUnitsReadback.setText(value);
		}
	}
	
	/**
	 * Returns the currently displayed PV.
	 * 
	 * @return pv name or null
	 */
	public ProcessVariable getPVName() {
		return this.PVName;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
