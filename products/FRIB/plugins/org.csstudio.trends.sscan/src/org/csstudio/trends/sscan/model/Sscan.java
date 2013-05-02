package org.csstudio.trends.sscan.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Sscan {
	
	private Model model = null;

    /** Positioner configurations */
    final private ArrayList<Positioner> positionerList = new ArrayList<Positioner>();
    
    /** Detector configurations */
    final private ArrayList<Detector> detectorList = new ArrayList<Detector>();

	private String pvName;
	private int scanNumber;

	Sscan(String pvName, int scanNumber){
		
			this.pvName = pvName;
			this.scanNumber = scanNumber;
            addPositioners(pvName);
            addDetectors(pvName);

	}
	
	public String getName(){
		return pvName;
	}
	
	private void addPositioners(String name) {
	    	for(int i=1; i<=4; i++){
	    		Positioner positioner = new Positioner(Integer.valueOf(i),name);
	    		positionerList.add(positioner);
	    	}
		
	}
	    
	private void addDetectors(String name) {
	    	for(int i=1; i<=9; i++){
	    		Detector detector = new Detector(Integer.valueOf(i),name);
	    		detectorList.add(detector);
	    	}
		
	}

	public int getPositionerCount() {
		return positionerList.size();
	}

	public Positioner getPositioner(int i) {
		return positionerList.get(i);
	}

	public int getPositionerIndex(Positioner positioner) {
		return positionerList.indexOf(positioner);
	}

	public int getDetectorCount() {
		return detectorList.size();
	}

	public Detector getDetector(int i) {
		return detectorList.get(i);
	}

	public int getDetectorIndex(Detector detector) {
		return detectorList.indexOf(detector);
	}
	
    public void close()
    {
    	for(Positioner positioner: positionerList){
    		positioner.close();
    	}
    	for(Detector detector: detectorList){
    		detector.close();
    	}
    }

	public void setModel(Model model) {
		this.model = model;
		
	}
}
