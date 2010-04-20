package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLDynamicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class TextWidget extends ADLAbstractWidget {
	private String textix;
	private String alignment;
	private String clrmod;
	private String format;
	
	public TextWidget(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	if (childWidget.getType().equals("basic attribute")){
	        		_adlBasicAttribute = new ADLBasicAttribute(childWidget);
	        		System.out.println("TextWidget Color " + _adlBasicAttribute.getClr());
	        		if (_adlBasicAttribute != null){
	        			_hasBasicAttribute = true;
	        		}
	        	}
	        	else if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        	else if (childWidget.getType().equals("dynamic attribute")){
	        		_adlDynamicAttribute = new ADLDynamicAttribute(childWidget);
	        		if (_adlDynamicAttribute != null){
	        			_hasDynamicAttribute = true;
	        		}
	        	}
	        }
			for (FileLine fileLine : adlWidget.getBody()){
				String bodyPart = fileLine.getLine();
				String[] row = bodyPart.trim().split("=");
				if (row.length < 2){
					throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
				}
				if (row[0].equals("textix")){
					setTextix(row[1].replaceAll("\"", "").trim());
				}
				else if (row[0].equals("alignment")){
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

	private void setTextix(String inString){
		textix = inString;
	}
	/**
	 * @return the textix
	 */
	public String getTextix() {
		return textix;
	}

	/**
	 * @param alignment the alignment to set
	 */
	private void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	/**
	 * @return the alignment
	 */
	public String getAlignment() {
		return alignment;
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
