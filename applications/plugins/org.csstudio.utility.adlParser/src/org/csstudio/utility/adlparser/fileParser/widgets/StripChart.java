package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

public class StripChart extends ADLAbstractWidget {

	public StripChart(ADLWidget adlWidget) {
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
		//TODO Add Title to StripChart
		//TODO Add X & Y Label to StripChart
		//TODO Add Foreground & Background colors to StripChart
		//TODO Add Period to StripChart
		//TODO Add Units to StripChart
		//TODO Add Channel/Color to StripChart
	}

}
