package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

public class CartesianPlot extends ADLAbstractWidget {

	public CartesianPlot(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        }
		}
		catch (WrongADLFormatException ex) {
			ex.printStackTrace();
		}
		// TODO Add Title to CartesianPlot
		// TODO Add X & Y Labels to CartesianPlot
		// TODO Add Plot Style to CartesianPlot
		// TODO Add Trace data to CartesianPlot
		// TODO Add Axis Data to CartesianPlot
		// TODO Add CountNum or channel to CartesianPlot
		// TODO Add Trigger Channel to CartesianPlot
		// TODO Add EraseChannel to CartesianPlot
		// TODO Add EraseMode to CartesianPlot
	}

}
