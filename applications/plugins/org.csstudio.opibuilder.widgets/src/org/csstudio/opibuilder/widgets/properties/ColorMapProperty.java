/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.properties;

import java.util.LinkedHashMap;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The property for script.
 * @author Xihui Chen
 *
 */
public class ColorMapProperty extends AbstractWidgetProperty {
	
	/**
	 * XML ELEMENT name <code>PREDEFINEDCOLOR</code>.
	 */
	public static final String XML_ELEMENT_MAP = "map"; //$NON-NLS-1$
	/**
	 * XML ELEMENT name <code>MAP</code>.
	 */
	public static final String XML_ELEMENT_E = "e"; //$NON-NLS-1$
	/**
	 * XML Element name <code>INTERPOLATE</code>.
	 */
	public static final String XML_ELEMENT_INTERPOLATE = "interpolate"; //$NON-NLS-1$
	/**
	 * XML Element name <code>AUTOSCALE</code>.
	 */
	public static final String XML_ELEMENT_AUTOSCALE = "autoscale"; //$NON-NLS-1$
	
	/**
	 * XML attribute name <code>red</code>.
	 */
	public static final String XML_ATTRIBUTE_RED = "red"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>green</code>.
	 */
	public static final String XML_ATTRIBUTE_GREEN = "green"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>blue</code>.
	 */
	public static final String XML_ATTRIBUTE_BLUE = "blue"; //$NON-NLS-1$	
	
	

	public ColorMapProperty(String prop_id, String description,
			WidgetPropertyCategory category, ColorMap defaultValue) {
		super(prop_id, description, category, defaultValue);
		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		ColorMap acceptableValue = null;
		if(value instanceof ColorMap){
			if(((ColorMap)value).getMap().size() >=2)
				acceptableValue = (ColorMap)value;			
		}else if (value instanceof String){
			for(PredefinedColorMap map : ColorMap.PredefinedColorMap.values()){
				if(map.toString().equals(value)){
					acceptableValue = new ColorMap(map, true, true);
				break;
				}
			}

		}
		
		return acceptableValue;
	}

	
	
	
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new ColorMapPropertyDescriptor(prop_id, description, (IntensityGraphModel) widgetModel);
	}

	@Override
	public ColorMap readValueFromXML(Element propElement) {
		ColorMap result = new ColorMap();
		result.setInterpolate(Boolean.parseBoolean(
				propElement.getChild(XML_ELEMENT_INTERPOLATE).getValue()));
		result.setAutoScale(Boolean.parseBoolean(
				propElement.getChild(XML_ELEMENT_AUTOSCALE).getValue()));
		if(propElement.getChild(XML_ELEMENT_MAP).getChildren().size() ==0){
			PredefinedColorMap p = PredefinedColorMap.fromIndex(Integer.parseInt(
				propElement.getChild(XML_ELEMENT_MAP).getValue()));
			result.setPredefinedColorMap(p);
		}else{
			LinkedHashMap<Double, RGB> map = new LinkedHashMap<Double, RGB>();
			for(Object o : propElement.getChild(XML_ELEMENT_MAP).getChildren()){
				Element e = (Element)o;
				map.put(Double.parseDouble(e.getValue()), 
						new RGB(Integer.parseInt(e.getAttributeValue(XML_ATTRIBUTE_RED)),
								Integer.parseInt(e.getAttributeValue(XML_ATTRIBUTE_GREEN)),
								Integer.parseInt(e.getAttributeValue(XML_ATTRIBUTE_BLUE))));
			}
			result.setColorMap(map);
		}
		
		return result;
	}

	@Override
	public void writeToXML(Element propElement) {
		ColorMap colorMap = (ColorMap)getPropertyValue();
		Element interpolateElement = new Element(XML_ELEMENT_INTERPOLATE);
		interpolateElement.setText(Boolean.toString(colorMap.isInterpolate()));
		Element autoScaleElement = new Element(XML_ELEMENT_AUTOSCALE);
		autoScaleElement.setText(Boolean.toString(colorMap.isAutoScale()));
		
		Element preDefinedElement = new Element(XML_ELEMENT_MAP);		
		if(colorMap.getPredefinedColorMap() == PredefinedColorMap.None){
			for(Double k : colorMap.getMap().keySet()){
				Element colorElement = new Element(XML_ELEMENT_E);
				colorElement.setText(k.toString());
				RGB color = colorMap.getMap().get(k);
				colorElement.setAttribute(XML_ATTRIBUTE_RED, "" + color.red); //$NON-NLS-1$
				colorElement.setAttribute(XML_ATTRIBUTE_GREEN, "" + color.green); //$NON-NLS-1$
				colorElement.setAttribute(XML_ATTRIBUTE_BLUE, "" + color.blue); //$NON-NLS-1$
				preDefinedElement.addContent(colorElement);
			}			
		}else{
			preDefinedElement.setText(Integer.toString(
				PredefinedColorMap.toIndex(colorMap.getPredefinedColorMap()))); //$NON-NLS-1$
		}
		propElement.addContent(interpolateElement);
		propElement.addContent(autoScaleElement);
		propElement.addContent(preDefinedElement);
	}

	
	@Override
	public boolean configurableByRule() {
		return true;
	}
	
	@Override
	public boolean onlyAcceptExpressionInRule() {
		return true;
	}
	
}
