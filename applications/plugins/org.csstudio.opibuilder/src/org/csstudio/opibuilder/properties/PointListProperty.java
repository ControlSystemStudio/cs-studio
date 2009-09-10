package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PointlistPropertyDescriptor;
import org.csstudio.opibuilder.properties.support.ScriptPropertyDescriptor;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The property for script.
 * @author Xihui Chen
 *
 */
public class PointListProperty extends AbstractWidgetProperty {
	
	/**
	 * XML ELEMENT name <code>POINT</code>.
	 */
	public static final String XML_ELEMENT_POINT= "point"; //$NON-NLS-1$

	/**
	 * XML ATTRIBUTE name <code>X</code>.
	 */
	public static final String XML_ATTRIBUTE_X = "x"; //$NON-NLS-1$
	
	/**
	 * XML ATTRIBUTE name <code>Y</code>.
	 */
	public static final String XML_ATTRIBUTE_Y = "y"; //$NON-NLS-1$
	


	public PointListProperty(String prop_id, String description,
			WidgetPropertyCategory category, PointList defaultValue) {
		super(prop_id, description, category, defaultValue);
		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		PointList acceptableValue = null;
		if(value instanceof PointList){
			acceptableValue = (PointList)value;			
		}
		
		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new PointlistPropertyDescriptor(prop_id, description);
	}

	@Override
	public PointList readValueFromXML(Element propElement) {
		PointList result = new PointList();
		for(Object oe : propElement.getChildren(XML_ELEMENT_POINT)){
			Element se = (Element)oe;	
			result.addPoint(Integer.parseInt(se.getAttributeValue(XML_ATTRIBUTE_X)),
					Integer.parseInt(se.getAttributeValue(XML_ATTRIBUTE_Y)));		
		}		
		return result;
	}

	@Override
	public void writeToXML(Element propElement) {
		int size = ((PointList)getPropertyValue()).size();		
		for(int i=0; i<size; i++){			
				Point point = ((PointList)getPropertyValue()).getPoint(i);				
				Element pointElement = new Element(XML_ELEMENT_POINT);
				pointElement.setAttribute(XML_ATTRIBUTE_X, 
						"" + point.x);				
				pointElement.setAttribute(XML_ATTRIBUTE_Y, 
						"" + point.y);
				propElement.addContent(pointElement);
		}		
	}

}
