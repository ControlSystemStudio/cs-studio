package org.csstudio.utility.adlparser.fileParser.widgets;

import java.util.ArrayList;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLPen;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLPlotcom;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class StripChart extends ADLAbstractWidget {
	private String units = new String("second");
	private int period = 60;
	private ArrayList<ADLPen> pens = new ArrayList<ADLPen>();
	public StripChart(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        	if (childWidget.getType().equals("plotcom")){
	        		_adlPlotcom = new ADLPlotcom(childWidget);
	        		if (_adlPlotcom != null){
	        			_hasPlotcom = true;
	        		}
	        	}
	        	if (childWidget.getType().equals("plotcom")){
	        		pens.add(new ADLPen(childWidget));
	        	}
	        }
			for (FileLine fileLine : adlWidget.getBody()){
				String bodyPart = fileLine.getLine();
				String[] row = bodyPart.trim().split("=");
				if (row.length < 2){
					throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
				}
				if (FileLine.argEquals(row[0], "period")){
					setPeriod(FileLine.getIntValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "units")){
					setUnits(FileLine.getTrimmedValue(row[1]));
				}
			}
		}
		
		catch (WrongADLFormatException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param units the units to set
	 */
	private void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param period the period to set
	 */
	private void setPeriod(int period) {
		this.period = period;
	}

	/**
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * @return the pens
	 */
	public ArrayList<ADLPen> getPens() {
		return pens;
	}

}
