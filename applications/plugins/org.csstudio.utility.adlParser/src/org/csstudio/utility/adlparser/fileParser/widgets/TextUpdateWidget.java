package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

public class TextUpdateWidget extends ADLAbstractWidget {

	public TextUpdateWidget(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        	else if (childWidget.getType().equals("monitor")){
	        		_adlMonitor = new ADLMonitor(childWidget);
	        		if (_adlMonitor != null){
	        			_hasMonitor = true;
	        		}
	        	}
	        }
		}
		catch (WrongADLFormatException ex) {
			ex.printStackTrace();
		}
		//TODO Add PVLimits to TextUpdateWidget
		//TODO Add Format to TextUpdateWidget
		//TODO Add ColorMode to TextUpdateWidget
		
	}

}
