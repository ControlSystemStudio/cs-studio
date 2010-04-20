package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

public class ChoiceButton extends ADLAbstractWidget {

	public ChoiceButton(ADLWidget adlWidget) {
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
		}
		catch (WrongADLFormatException ex) {
			ex.printStackTrace();
		}
		// TODO Add Color mode to ChoiceButton
		// TODO Add Stacking to ChoiceButton
	}

}
