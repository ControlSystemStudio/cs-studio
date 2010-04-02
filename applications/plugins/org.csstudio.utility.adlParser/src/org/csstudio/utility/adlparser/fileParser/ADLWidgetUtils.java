package org.csstudio.utility.adlparser.fileParser;

import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLDynamicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLPoints;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;

public class ADLWidgetUtils {
	public static Object[] getADLWidgetChildren(ADLWidget adlWidget){
		try {
			System.out.println("getADLWidgetChildren " + adlWidget.getType());
			if ( adlWidget.getType().equals("basic attribute")){
				return (new ADLBasicAttribute(adlWidget)).getChildren();
			}
			else if ( adlWidget.getType().equals("object")){
				return (new ADLObject(adlWidget)).getChildren();
			}
			else if ( adlWidget.getType().equals("control")){
				return (new ADLControl(adlWidget)).getChildren();
			}
			else if ( adlWidget.getType().equals("monitor")){
				return (new ADLMonitor(adlWidget)).getChildren();
			}
			else if ( adlWidget.getType().equals("points")){
				return (new ADLPoints(adlWidget)).getChildren();
			}
			else if ( adlWidget.getType().contains("display[")){
				return (new RelatedDisplayItem(adlWidget)).getChildren();
			}
			else if ( adlWidget.getType().equals("dynamic attribute")){
				System.out.println("Catching dynamic attribute");
				return (new ADLDynamicAttribute(adlWidget)).getChildren();
			}

		}
		catch (WrongADLFormatException ex){
			System.out.println("problem parsing " + adlWidget.getType());
			ex.printStackTrace();
		}
		return adlWidget.getObjects().toArray();
	}
	
}
