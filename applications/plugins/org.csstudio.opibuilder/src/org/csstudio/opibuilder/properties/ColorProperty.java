/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The widget property for color.
 * @author Xihui Chen
 *
 */
public class ColorProperty extends AbstractWidgetProperty {
	
	/**
	 * XML attribute name <code>color</code>.
	 */
	public static final String XML_ELEMENT_COLOR = "color"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>color</code>.
	 */
	public static final String XML_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

	
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

	private static final String QUOTE = "\""; //$NON-NLS-1$
	
	/**Color Property Constructor. The property value type is {@link OPIColor}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 */
	public ColorProperty(String prop_id, String description,
			WidgetPropertyCategory category, RGB defaultValue) {
		super(prop_id, description, category, new OPIColor(defaultValue));
	}
	
	/**Color Property Constructor. The property value type is {@link OPIColor}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created. It must be 
	 * a color macro name in color file.
	 */
	public ColorProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		super(prop_id, description, category, 
				MediaService.getInstance().getOPIColor(defaultValue));
	}
	

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#checkValue(java.lang.Object)
	 */
	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		
		Object acceptedValue = value;

		
		if (value instanceof OPIColor) {
			if(((OPIColor)value).getRGBValue() == null)
				acceptedValue = null;
		}else if(value instanceof RGB){
			acceptedValue = new OPIColor((RGB)value);
		}else if(value instanceof String){
			acceptedValue = MediaService.getInstance().getOPIColor((String)value);
		}else
			acceptedValue = null;
			
		
		return acceptedValue;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#createPropertyDescriptor()
	 */
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getOPIColorPropertyDescriptor(prop_id, description);		
	}

	@Override
	public void writeToXML(Element propElement) {
		OPIColor opiColor = (OPIColor) getPropertyValue();
		Element colorElement;
		colorElement= new Element(XML_ELEMENT_COLOR);
		if(opiColor.isPreDefined()){			
			colorElement.setAttribute(XML_ATTRIBUTE_NAME, opiColor.getColorName());			
		}
		RGB color = opiColor.getRGBValue();
		colorElement.setAttribute(XML_ATTRIBUTE_RED, "" + color.red); //$NON-NLS-1$
		colorElement.setAttribute(XML_ATTRIBUTE_GREEN, "" + color.green); //$NON-NLS-1$
		colorElement.setAttribute(XML_ATTRIBUTE_BLUE, "" + color.blue); //$NON-NLS-1$
		propElement.addContent(colorElement);
	}
	
	
	@Override
	public Object readValueFromXML(Element propElement) {
		Element colorElement = propElement.getChild(XML_ELEMENT_COLOR);
		String name = colorElement.getAttributeValue(XML_ATTRIBUTE_NAME);
		if(name == null) {
				RGB result = new RGB(Integer.parseInt(colorElement.getAttributeValue(XML_ATTRIBUTE_RED)),
				Integer.parseInt(colorElement.getAttributeValue(XML_ATTRIBUTE_GREEN)),
				Integer.parseInt(colorElement.getAttributeValue(XML_ATTRIBUTE_BLUE)));
				return new OPIColor(result);		
		}else{
			String red = colorElement.getAttributeValue(XML_ATTRIBUTE_RED);
			String green = colorElement.getAttributeValue(XML_ATTRIBUTE_GREEN);
			String blue = colorElement.getAttributeValue(XML_ATTRIBUTE_BLUE);
			RGB rgb;
			if(red != null && green != null && blue !=null){
				rgb=new RGB(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
				return MediaService.getInstance().getOPIColor(name, rgb);
			}
			return MediaService.getInstance().getOPIColor(name);
		}
	
	}

	@Override
	public boolean configurableByRule() {
		return true;
	}
	
	@Override
	public String toStringInRuleScript(Object propValue) {
		OPIColor opiColor = (OPIColor)propValue;
		if(opiColor.isPreDefined())
			return QUOTE + opiColor.getColorName()+QUOTE;
		else{
			RGB rgb = opiColor.getRGBValue();
			return "ColorFontUtil.getColorFromRGB("+ //$NON-NLS-1$
				rgb.red + "," + rgb.green + "," + rgb.blue + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}	
}
