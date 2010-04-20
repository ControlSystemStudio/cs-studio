package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class MessageButton extends ADLAbstractWidget {
	private String label = new String();;
	private String press_msg = new String();
	private String release_msg = new String();

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
				else if (row[0].equals("press_msg")){
					setPress_msg(row[1].replaceAll("\"", ""));
				}
				else if (row[0].equals("release_msg")){
					setRelease_msg(row[1].replaceAll("\"", ""));
				}
			}
		}
		catch (WrongADLFormatException ex) {
			
		}
		//TODO Add Color mode to MessageButton
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
	 * @param press_msg the press_msg to set
	 */
	public void setPress_msg(String press_msg) {
		this.press_msg = press_msg;
	}

	/**
	 * @return the press_msg
	 */
	public String getPress_msg() {
		return press_msg;
	}

	/**
	 * @param release_msg the release_msg to set
	 */
	public void setRelease_msg(String release_msg) {
		this.release_msg = release_msg;
	}

	/**
	 * @return the release_msg
	 */
	public String getRelease_msg() {
		return release_msg;
	}

}
