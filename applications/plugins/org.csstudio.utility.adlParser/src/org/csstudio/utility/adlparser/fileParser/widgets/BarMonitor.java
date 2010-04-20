package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLLimits;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class BarMonitor extends ADLAbstractWidget {
	private String color_mode = new String("static");
	private String direction = new String("right");
	private String fill_mode = new String("from edge");
	private String label = new String("none");

	public BarMonitor(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	if (childWidget.getType().equals("monitor")){
	        		_adlMonitor = new ADLMonitor(childWidget);
	        		if (_adlMonitor != null){
	        			_hasMonitor = true;
	        		}
	        	}
	        	else if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        	else if (childWidget.getType().equals("limits")){
	        		_adlLimits = new ADLLimits(childWidget);
	        		if (_adlLimits != null){
	        			_hasLimits = true;
	        		}
	        	}
	        }
			for (FileLine fileLine : adlWidget.getBody()){
				String bodyPart = fileLine.getLine();
				String[] row = bodyPart.trim().split("=");
				if (row.length < 2){
					throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
				}
				if (FileLine.argEquals(row[0], "clrmod")){
					setColor_mode(FileLine.getTrimmedValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "label")){
					setLabel(FileLine.getTrimmedValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "direction")){
					setDirection(FileLine.getTrimmedValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "fillmod")){
					setDirection(FileLine.getTrimmedValue(row[1]));
				}
			}
		}
		catch (WrongADLFormatException ex) {
			
		}


	}
	/**
	 * @param color_mode the color_mode to set
	 */
	private void setColor_mode(String color_mode) {
		this.color_mode = color_mode;
	}

	/**
	 * @return the color_mode
	 */
	public String getColor_mode() {
		return color_mode;
	}
	/**
	 * @param direction the direction to set
	 */
	private void setDirection(String direction) {
		this.direction = direction;
	}
	/**
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}
	/**
	 * @param fill_mode the fill_mode to set
	 */
	private void setFill_mode(String fill_mode) {
		this.fill_mode = fill_mode;
	}
	/**
	 * @return the fill_mode
	 */
	public String getFill_mode() {
		return fill_mode;
	}
	/**
	 * @param label the label to set
	 */
	private void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


}
