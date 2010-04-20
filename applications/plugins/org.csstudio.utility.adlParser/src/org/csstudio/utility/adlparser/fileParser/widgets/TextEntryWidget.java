package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

public class TextEntryWidget extends ADLAbstractWidget {

	public TextEntryWidget(ADLWidget adlWidget) {
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
			
		}
		//TODO Add PVLimits to TextEntry
		//TODO Add Format to TextEntry
		//TODO Add ColorMode to TextEntry
		
	}

}


