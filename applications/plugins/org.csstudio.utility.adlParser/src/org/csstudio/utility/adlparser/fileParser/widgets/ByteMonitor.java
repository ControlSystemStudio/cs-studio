package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

public class ByteMonitor extends ADLAbstractWidget {

	public ByteMonitor(ADLWidget adlWidget) {
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

		// TODO Add Direction to ByteMonitor
		// TODO Add ColorMode to ByteMonitor
		// TODO Add start bit & end bit to Byte Monitor
	}

}
