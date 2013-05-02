package org.csstudio.trends.sscan.model;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.hz;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VInt;
import org.epics.pvmanager.data.VString;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

public class Positioner {

	private String name;
	private Integer positionerIndex;
	
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	private PVReader<Map<String, Object>> pv;
	
	private String positionerPV;	//	PnPV	Positioner n Process Variable ame	STRING [40]
	private Integer nameValid;		//	PnNV	PnPV Name Valid	LONG
	private String stepMode;		//	PnSM	Positioner n step-mode	Menu ("LINEAR", "TABLE", "FLY")
	private String absRelMode;		//	PnAR	Positioner n Absolute/Relative Mode	Menu ("ABSOLUTE", "RELATIVE")
	private Double desiredValue;	//	PnDV	Pos. n Desired Value	DOUBLE
	private Double lastValue;		//	PnLV	Pos. n Last Value	DOUBLE
	private String engUnits;		//	PnEU	Positioner n Eng. Units	STRING [16]
	private Double highRange;		//	PnHR	Pos. n High Range	DOUBLE
	private Double lowRange;		//	PnLR	Pos. n Low Range	DOUBLE
	private Integer prec;			//	PnPR	Pos. n Precision	SHORT
	private double[] stepArray;		//	PnPA	Pn Step Array	DOUBLE[]
	private String readbackPV;		//  RnPV	Readback n Process Variable 	STRING [40]
	private Integer rbNameValid;	//  RnNV	Readback /n Name Valid	LONG
	private Double rbDiffLimit;		//  RnDL	Readback n Difference Limit 	DOUBLE
	private Double rbCurrentValue;	//  RnCV	Readback n Current Value	DOUBLE
	private Double rbLastValue;		//  RnLV	Readback n Last Value	DOUBLE
	private double[] readbackData;	//  PnRA	Pn Readback Array	DOUBLE[]
	private double[] currentData;	//  PnCA	Pn Current Readback Array	DOUBLE[]
	private Integer currentPoint;	//  CPT		Current Point
	
	
	public void close(){
		pv.close();
	}
	
	Positioner(Integer positionerIndex, String name) {
		this.positionerIndex = positionerIndex;
		this.name = name;
		//TODO: make readWrite and remove Writers in sets when readWrite bug is fixed
		this.pv = PVManager.read(mapOf(latestValueOf(channel(name+".P"+positionerIndex.toString()+"PV").as("positionerPV")
				.and(channel(name+".P"+positionerIndex.toString()+"NV").as("nameValid"))
				.and(channel(name+".P"+positionerIndex.toString()+"SM").as("stepMode"))
				.and(channel(name+".P"+positionerIndex.toString()+"AR").as("absRelMode"))
				.and(channel(name+".P"+positionerIndex.toString()+"DV").as("desiredValue"))
				.and(channel(name+".P"+positionerIndex.toString()+"LV").as("lastValue"))
				.and(channel(name+".P"+positionerIndex.toString()+"EU").as("engUnits"))
				.and(channel(name+".P"+positionerIndex.toString()+"HR").as("highRange"))
				.and(channel(name+".P"+positionerIndex.toString()+"LR").as("lowRange"))
				.and(channel(name+".P"+positionerIndex.toString()+"PR").as("prec"))
				.and(channel(name+".P"+positionerIndex.toString()+"PA").as("stepArray"))
				.and(channel(name+".R"+positionerIndex.toString()+"PV").as("readbackPV"))
				.and(channel(name+".R"+positionerIndex.toString()+"NV").as("rbNameValid"))
				.and(channel(name+".R"+positionerIndex.toString()+"DL").as("rbDiffLimit"))
				.and(channel(name+".R"+positionerIndex.toString()+"CV").as("rbCurrentValue"))
				.and(channel(name+".R"+positionerIndex.toString()+"LV").as("rbLastValue"))
				.and(channel(name+".P"+positionerIndex.toString()+"RA").as("readbackData"))
				.and(channel(name+".P"+positionerIndex.toString()+"CA").as("currentData"))
				.and(channel(name+".CPT").as("currentPoint")))))
				.notifyOn(swtThread())
				.every(hz(25));
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Map<String, Object> map = pv.getValue();
				VString pPV = (VString)map.get("positionerPV");
				Object pnameValid = map.get("nameValid");
				Object pstepMode = map.get("stepMode");
				Object pabsRelMode = map.get("absRelMode");
				Object pdesiredValue = map.get("desiredValue");
				Object plastValue = map.get("lastValue");
				VString pengUnits = (VString)map.get("engUnits");
				Object phighRange = map.get("highRange");
				Object plowRange = map.get("lowRange");
				Object pprec = map.get("prec");
				Object pstepArray = map.get("stepArray");
				VString preadbackPV = (VString)map.get("readbackPV");
				Object prbNameValid = map.get("rbNameValid");
				Object prbDiffLimit = map.get("rbDiffLimit");
				Object prbCurrentValue = map.get("rbCurrentValue");
				Object prbLastValue = map.get("rbLastValue");
				Object preadbackData = map.get("readbackData");
				Object pcurrentData = map.get("currentData");
				Object pcurrentPoint = map.get("currentPoint");
				
				setPV(pPV);
				setnameValid(pnameValid);
				setstepMode(pstepMode);
				setabsRelMode(pabsRelMode);
				setdesiredValue(pdesiredValue);
				setlastValue(plastValue);
				setengUnits(pengUnits);
				sethighRange(phighRange);
				setlowRange(plowRange);
				setprec(pprec);
				setstepArray(pstepArray);
				setreadbackPV(preadbackPV);
				setrbNameValid(prbNameValid);
				setrbDiffLimit(prbDiffLimit);
				setrbCurrentValue(prbCurrentValue);
				setrbLastValue(prbLastValue);
				setreadbackData(preadbackData);
				setcurrentData(pcurrentData);
				setcurrentPoint(pcurrentPoint);
				
				fireChange(preadbackData);
			}
		});
	}

	private void fireChange(Object readbackData) {
		if(readbackData!=null){
			Object oldreadbackData = this.readbackData;
			this.readbackData = ((VDoubleArray)readbackData).getArray();
			changeSupport.firePropertyChange("positioner", oldreadbackData, readbackData);
		}
	}
	
	private void setcurrentPoint(Object pcurrentPoint) {
		if(pcurrentPoint!=null)
			this.currentPoint = ((VInt)pcurrentPoint).getValue();
	}
	
	private void setcurrentData(Object pcurrentData) {
		if(pcurrentData!=null)
			this.currentData = ((VDoubleArray)pcurrentData).getArray();
	}

	private void setreadbackData(Object preadbackData) {
		if(preadbackData!=null)
			this.readbackData = ((VDoubleArray)preadbackData).getArray();
	}

	private void setrbLastValue(Object prbLastValue) {
		if(prbLastValue!=null)
			this.rbLastValue = ((VDouble)prbLastValue).getValue();
	}

	private void setrbCurrentValue(Object prbCurrentValue) {
		if(prbCurrentValue!=null)
			this.rbCurrentValue = ((VDouble)prbCurrentValue).getValue();
	}

	private void setrbDiffLimit(Object prbDiffLimit) {
		if(prbDiffLimit!=null)
			this.rbDiffLimit = ((VDouble)prbDiffLimit).getValue();
	}

	private void setrbNameValid(Object prbNameValid) {
		if(prbNameValid!=null)
			this.rbNameValid = Integer.valueOf(((VEnum)prbNameValid).getIndex());
	}

	private void setreadbackPV(VString preadbackPV) {
		if(preadbackPV!=null)
			this.readbackPV = (preadbackPV.getValue());
	}

	private void setstepArray(Object pstepArray) {
		if(pstepArray!=null)
			this.stepArray = ((VDoubleArray)pstepArray).getArray();
	}

	private void setprec(Object pprec) {
		if(pprec!=null)
			this.prec = ((VInt)pprec).getValue();
	}

	private void setlowRange(Object plowRange) {
		if(plowRange!=null)
			this.lowRange = ((VDouble)plowRange).getValue();
	}

	private void sethighRange(Object phighRange) {
		if(phighRange!=null)
			this.highRange = ((VDouble)phighRange).getValue();	
	}

	private void setengUnits(VString pengUnits) {
		if(pengUnits!=null)
			this.engUnits = (pengUnits).getValue();	
	}

	private void setlastValue(Object plastValue) {
		if(plastValue!=null)
			this.lastValue = ((VDouble)plastValue).getValue();
	}

	private void setdesiredValue(Object pdesiredValue) {
		if(pdesiredValue!=null)
			this.desiredValue = ((VDouble)pdesiredValue).getValue();	
	}

	private void setabsRelMode(Object pabsRelMode) {
		if(pabsRelMode!=null)
			this.absRelMode = ((VEnum)pabsRelMode).getValue();		
	}

	private void setstepMode(Object pstepMode) {
		if(pstepMode!=null)
			this.stepMode = ((VEnum)pstepMode).getValue();		
	}

	private void setnameValid(Object pnameValid) {
		if(pnameValid!=null)
			this.nameValid = Integer.valueOf(((VEnum)pnameValid).getIndex());		
	}

	private void setPV(VString pPV) {
		if(pPV!=null)
			this.positionerPV = pPV.getValue();		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getId(){
		return positionerIndex;
	}
	
	public void setId(int id){
		this.positionerIndex = id;
	}

    public String getPositionerPV() {
		return positionerPV;
	}

	public void setPositionerPV(String positionerPV) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"PV")).async();		 
		pvWriter.write(positionerPV);
		pvWriter.close();
	}

	public Integer getNameValid() {
		return nameValid;
	}

	public void setNameValid(Integer nameValid) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"NV")).async();		 
		pvWriter.write(nameValid);
		pvWriter.close();
	}

	public String getStepMode() {
		return stepMode;
	}

	public void setStepMode(String stepMode) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"SM")).async();		 
		pvWriter.write(stepMode);
		pvWriter.close();
	}

	public String getAbsRelMode() {
		return absRelMode;
	}

	public void setAbsRelMode(String absRelMode) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"AR")).async();		 
		pvWriter.write(absRelMode);
		pvWriter.close();
	}

	public Double getDesiredValue() {
		return desiredValue;
	}

	public Double getLastValue() {
		return lastValue;
	}

	public String getEngUnits() {
		return engUnits;
	}

	public void setEngUnits(String engUnits) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"EU")).async();		 
		pvWriter.write(engUnits);
		pvWriter.close();
	}

	public Double getHighRange() {
		return highRange;
	}

	public void setHighRange(Double highRange) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"HR")).async();		 
		pvWriter.write(highRange);
		pvWriter.close();
	}

	public Double getLowRange() {
		return lowRange;
	}

	public void setLowRange(Double lowRange) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"LR")).async();		 
		pvWriter.write(lowRange);
		pvWriter.close();
	}

	public Integer getPrec() {
		return prec;
	}

	public void setPrec(Integer prec) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"PR")).async();		 
		pvWriter.write(prec);
		pvWriter.close();
	}

	public double[] getStepArray() {
		return stepArray;
	}

	public void setStepArray(double[] stepArray) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".P"+positionerIndex.toString()+"SA")).async();		 
		pvWriter.write(stepArray);
		pvWriter.close();
	}

	public String getReadbackPV() {
		return readbackPV;
	}

	public void setReadbackPV(String readbackPV) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".R"+positionerIndex.toString()+"PV")).async();		 
		pvWriter.write(readbackPV);
		pvWriter.close();
	}

	public Integer getRbNameValid() {
		return rbNameValid;
	}

	public void setRbNameValid(Integer rbNameValid) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".R"+positionerIndex.toString()+"NV")).async();		 
		pvWriter.write(rbNameValid);
		pvWriter.close();
	}

	public Double getRbDiffLimit() {
		return rbDiffLimit;
	}

	public void setRbDiffLimit(Double rbDiffLimit) {
		PVWriter<Object> pvWriter = PVManager.write(channel(name+".R"+positionerIndex.toString()+"DL")).async();		 
		pvWriter.write(rbDiffLimit);
		pvWriter.close();
	}

	public Double getRbCurrentValue() {
		return rbCurrentValue;
	}

	public Double getRbLastValue() {
		return rbLastValue;
	}

	public double[] getReadbackData() {
		return readbackData;
	}

	public double[] getCurrentData() {
		return currentData;
	}
	
	public Integer getCurrentPoint() {
		return currentPoint;
	}

	public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }

}
