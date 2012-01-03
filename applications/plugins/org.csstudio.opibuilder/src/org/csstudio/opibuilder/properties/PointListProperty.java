/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The property for script.
 * 
 * @author Sven Wende (class of same name in SDS)
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
	

	/**PointList Property Constructor. The property value type is {@link PointList}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created. cannot be null.
	 */
	public PointListProperty(String prop_id, String description,
			WidgetPropertyCategory category, PointList defaultValue) {
		super(prop_id, description, category, defaultValue);
		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return new PointList();
		PointList acceptableValue = null;
		if(value instanceof PointList){
			acceptableValue = (PointList)value;			
		}else if (value instanceof int[])
			acceptableValue = new PointList((int[]) value);
		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getPointlistPropertyDescriptor(prop_id, description);
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
