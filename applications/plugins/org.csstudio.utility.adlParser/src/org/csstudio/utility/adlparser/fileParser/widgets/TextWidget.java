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
				if (FileLine.argEquals(row[0], "textix")){
					setTextix(FileLine.getTrimmedValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "alignment")){
					setAlignment(FileLine.getTrimmedValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "clrmod")){
					setClrmod(FileLine.getTrimmedValue(row[1]));
				}
				else if (FileLine.argEquals(row[0], "format")){
					setFormat(FileLine.getTrimmedValue(row[1]));
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
