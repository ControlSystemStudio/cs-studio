package org.csstudio.utility.adlparser.fileParser.widgets;

import java.util.ArrayList;

import org.csstudio.utility.adlparser.Activator;
import org.csstudio.utility.adlparser.IImageKeys;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;

/**
 * 
 * @author hammonds
 *
 */
public class ChoiceButton extends ADLAbstractWidget {

	public ChoiceButton(ADLWidget adlWidget) {
		super(adlWidget);
		name = new String("choice button");
		descriptor = Activator.getImageDescriptor(IImageKeys.ADL_CHOICE_BUTTON);
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

	@Override
	public Object[] getChildren() {
		ArrayList<Object> ret = new ArrayList<Object>();
		if (_hasObject) ret.add( _adlObject);
		if (_hasControl) ret.add( _adlControl);
		return ret.toArray();
	}

}
