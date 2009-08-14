
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.OPIColorPropertyDescriptor;
import org.csstudio.opibuilder.util.ColorService;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * @author Xihui Chen
 *
 */
public class ColorProperty extends AbstractWidgetProperty {
	
	/**
	 * XML attribute name <code>color</code>.
	 */
	public static final String XML_ELEMENT_COLOR = "color"; //$NON-NLS-1$

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
	

	public ColorProperty(String prop_id, String description,
			WidgetPropertyCategory category, RGB defaultValue) {
		super(prop_id, description, category, new OPIColor(defaultValue));
	}
	
	public ColorProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		super(prop_id, description, category, 
				ColorService.getInstance().getOPIColor(defaultValue));
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
		}else
			acceptedValue = null;
			
		
		return acceptedValue;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#createPropertyDescriptor()
	 */
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new OPIColorPropertyDescriptor(prop_id, description);		
	}

	@Override
	public void writeToXML(Element propElement) {
		Element colorElement = new Element(XML_ELEMENT_COLOR);
		RGB color = ((OPIColor) getPropertyValue()).getRGBValue();
		colorElement.setAttribute(XML_ATTRIBUTE_RED, "" + color.red); //$NON-NLS-1$
		colorElement.setAttribute(XML_ATTRIBUTE_GREEN, "" + color.green); //$NON-NLS-1$
		colorElement.setAttribute(XML_ATTRIBUTE_BLUE, "" + color.blue); //$NON-NLS-1$

		propElement.addContent(colorElement);
	}
	
	
	@Override
	public Object readValueFromXML(Element propElement) {
		Element colorElement = propElement.getChild(XML_ELEMENT_COLOR);
		RGB result = new RGB(Integer.parseInt(colorElement.getAttributeValue(XML_ATTRIBUTE_RED)),
				Integer.parseInt(colorElement.getAttributeValue(XML_ATTRIBUTE_GREEN)),
				Integer.parseInt(colorElement.getAttributeValue(XML_ATTRIBUTE_BLUE)));
		return new OPIColor(result);
		
	}
	
	public void setPropertyValue(String name) {		
		setPropertyValue(new OPIColor(name));
	}
	
	
	public void setPropertyValue(RGB value) {		
		setPropertyValue(new OPIColor(value));
	}

}
