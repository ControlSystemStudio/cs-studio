package org.csstudio.trends.sscan.model;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.hz;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VFloatArray;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.VString;

public class Detector {
	private String name;
	private Integer detectorIndex;
	
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	private PVReader<Map<String, Object>> pv;
	
	private String detectorPV;		//	DnnPV	data nn Process Variable name	STRING [40]
	private Integer nameValid;		//	DnnNV	data nn Name Valid	LONG
	private float[] endOfScanData;	//	DnnDA	Detector nn End-Of-Scan Data Array	FLOAT[ ]
	private float[] currentData;	//	DnnCA	Detector nn Current-Data Array	FLOAT[ ]
	private String engUnits;		//	DnnEU	Detector nn Eng. Units	STRING [16]
	private Double highRange;		//	DnnHR	Det. nn High Range	DOUBLE
	private Double lowRange;		//	DnnLR	Det. nn Low Range	DOUBLE
	private Integer prec;			//	DnnPR	Det. nn Precision	SHORT
	private Double currentValue;	//	DnnCV	Detector nn Current Value	FLOAT
	private Double lastValue;		//	DnnLV	Detector nn Last Value	FLOAT
	
	public void close(){
		pv.close();
	}
	
	Detector(Integer detectorIndex, String name) {
		this.setDetectorIndex(detectorIndex);
		this.setName(name);
		//TODO: make readWrite and remove Writers in sets when readWrite bug is fixed
		this.pv = PVManager.read(mapOf(latestValueOf(channel(name+".D0"+detectorIndex.toString()+"PV").as("detectorPV")
				.and(channel(name+".D0"+detectorIndex.toString()+"NV").as("nameValid"))
				.and(channel(name+".D0"+detectorIndex.toString()+"DA").as("endOfScanData"))
				.and(channel(name+".D0"+detectorIndex.toString()+"CA").as("currentData"))
				.and(channel(name+".D0"+detectorIndex.toString()+"EU").as("engUnits"))
				.and(channel(name+".D0"+detectorIndex.toString()+"HR").as("highRange"))
				.and(channel(name+".D0"+detectorIndex.toString()+"LR").as("lowRange"))
				.and(channel(name+".D0"+detectorIndex.toString()+"PR").as("prec"))
				.and(channel(name+".D0"+detectorIndex.toString()+"CV").as("currentValue"))
				.and(channel(name+".D0"+detectorIndex.toString()+"LV").as("lastValue")))))
				.notifyOn(swtThread())
				.every(hz(25));
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Map<String, Object> map = pv.getValue();
				VString dPV = (VString)map.get("detectorPV");
				Object dnameValid = map.get("nameValid");
				Object dendOfScanData = map.get("endOfScanData");
				Object dcurrentData = map.get("currentData");
				VString dengUnits = (VString)map.get("engUnits");
				Object dhighRange = map.get("highRange");
				Object dlowRange = map.get("lowRange");
				Object dprec = map.get("prec");
				Object dcurrentValue = map.get("currentValue");
				Object dlastValue = map.get("lastValue");
				
				setPV(dPV);
				setnameValid(dnameValid);
				setendOfScanData(dendOfScanData);
				setcurrentData(dcurrentData);
				setengUnits(dengUnits);
				sethighRange(dhighRange);
				setlowRange(dlowRange);
				setprec(dprec);
				setcurrentValue(dcurrentValue);
				setlastValue(dlastValue);
				
				fireChange(pv);
			}
		});
	}
	private void fireChange(PVReader<Map<String, Object>> pv) {
		PVReader<Map<String, Object>> oldPv = this.pv;
		this.pv = pv;
		changeSupport.firePropertyChange("detector", oldPv, pv);	
	}

	private void setlastValue(Object dlastValue) {
		if(dlastValue!=null)
			this.lastValue = ((VDouble)dlastValue).getValue();
	}

	private void setcurrentValue(Object dcurrentValue) {
		if(dcurrentValue!=null)
			this.currentValue = ((VDouble)dcurrentValue).getValue();
	}

	private void setprec(Object dprec) {
		if(dprec!=null)
			this.prec = ((VInt)dprec).getValue();
	}

	private void setlowRange(Object dlowRange) {
		if(dlowRange!=null)
			this.lowRange = ((VDouble)dlowRange).getValue();
	}

	private void sethighRange(Object dhighRange) {
		if(dhighRange!=null)
			this.highRange = ((VDouble)dhighRange).getValue();
	}

	private void setengUnits(VString dengUnits) {
		if(dengUnits!=null)
			this.engUnits = dengUnits.getValue();
	}

	private void setcurrentData(Object dcurrentData) {
		if(dcurrentData!=null)
			this.currentData = ((VFloatArray)dcurrentData).getArray();
	}

	private void setendOfScanData(Object dendOfScanData) {
		if(dendOfScanData!=null)
			this.endOfScanData = ((VFloatArray)dendOfScanData).getArray();	
	}

	private void setnameValid(Object dnameValid) {
		if(dnameValid!=null)
			this.nameValid = ((VEnum)dnameValid).getIndex();
	}

	private void setPV(VString dPV) {
		if(dPV!=null)
			this.detectorPV = dPV.getValue();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDetectorIndex() {
		return detectorIndex;
	}

	public void setDetectorIndex(Integer detectorIndex) {
		this.detectorIndex = detectorIndex;
	}

	public String getDetectorPV() {
		return detectorPV;
	}

	public void setDetectorPV(String detectorPV) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".D0"+detectorIndex.toString()+"PV")).async();		 
		pvWriter.write(detectorPV);
		pvWriter.close();
	}

	public Integer getNameValid() {
		return nameValid;
	}

	public void setNameValid(Integer nameValid) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".D0"+detectorIndex.toString()+"NV")).async();		 
		pvWriter.write(nameValid);
		pvWriter.close();
	}

	public float[] getEndOfScanData() {
		return endOfScanData;
	}

	public float[] getCurrentData() {
		return currentData;
	}

	public String getEngUnits() {
		return engUnits;
	}

	public void setEngUnits(String engUnits) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".D0"+detectorIndex.toString()+"EU")).async();		 
		pvWriter.write(engUnits);
		pvWriter.close();
	}

	public Double getHighRange() {
		return highRange;
	}

	public void setHighRange(Double highRange) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".D0"+detectorIndex.toString()+"HR")).async();		 
		pvWriter.write(highRange);
		pvWriter.close();
	}

	public Double getLowRange() {
		return lowRange;
	}

	public void setLowRange(Double lowRange) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".D0"+detectorIndex.toString()+"LR")).async();		 
		pvWriter.write(lowRange);
		pvWriter.close();
	}

	public Integer getPrec() {
		return prec;
	}

	public void setPrec(Integer prec) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".D0"+detectorIndex.toString()+"PR")).async();		 
		pvWriter.write(prec);
		pvWriter.close();
	}

	public Double getCurrentValue() {
		return currentValue;
	}

	public Double getLastValue() {
		return lastValue;
	}
	

	public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
}
