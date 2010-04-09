package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class MessageButton extends ADLAbstractWidget {
	private String label;
	private String alignment;
	private String clrmod;
	private String format;

	public MessageButton(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	
	        	if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        	else if (childWidget.getType().equals("control")){
	        		_adlControl = new ADLControl(childWidget);
	        		if (_adlControl != null){
	        			_hasControl = true;
	        		}
	        	}
			}
			for (FileLine fileLine : adlWidget.getBody()){
				String bodyPart = fileLine.getLine();
				String[] row = bodyPart.trim().split("=");
				if (row.length < 2){
					throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
				}
				if (row[0].equals("label")){
					setLabel(row[1].replaceAll("\"", "").trim());
				}
				else if (row[0].equals("alingment")){
					setAlignment(row[1].replaceAll("\"", ""));
				}
				else if (row[0].equals("clrmod")){
					setClrmod(row[1].replaceAll("\"", ""));
				}
				else if (row[0].equals("format")){
					setFormat(row[1].replaceAll("\"", ""));
				}
			}
		}
		catch (WrongADLFormatException ex) {
			
		}
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param clrmod the clrmod to set
	 */
	public void setClrmod(String clrmod) {
		this.clrmod = clrmod;
	}

	/**
	 * @return the clrmod
	 */
	public String getClrmod() {
		return clrmod;
	}

	/**
	 * @param alignment the alignment to set
	 */
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	/**
	 * @return the alignment
	 */
	public String getAlignment() {
		return alignment;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

}
