
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.FontPropertyDescriptor;
import org.csstudio.opibuilder.properties.support.RGBColorPropertyDescriptor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * @author Xihui Chen
 *
 */
public class FontProperty extends AbstractWidgetProperty {

	/**
	 * XML attribute name <code>font</code>.
	 */
	public static final String XML_ELEMENT_FONT = "font"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>fontName</code>.
	 */
	public static final String XML_ATTRIBUTE_FONT_NAME = "fontName"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>fontName</code>.
	 */
	public static final String XML_ATTRIBUTE_FONT_HEIGHT = "height"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>fontName</code>.
	 */
	public static final String XML_ATTRIBUTE_FONT_STYLE = "style"; //$NON-NLS-1$
	
	public FontProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			FontData defaultValue) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#checkValue(java.lang.Object)
	 */
	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		
		Object acceptedValue = value;

		if (!(value instanceof FontData)) {
			acceptedValue = null;
		}
		
		return acceptedValue;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#createPropertyDescriptor()
	 */
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new FontPropertyDescriptor(prop_id, description);		
	}

	@Override
	public void writeToXML(Element propElement) {
		Element fontElement = new Element(XML_ELEMENT_FONT);

		FontData fontData = (FontData) propertyValue;
		fontElement.setAttribute(XML_ATTRIBUTE_FONT_NAME, fontData.getName());
		fontElement.setAttribute(XML_ATTRIBUTE_FONT_HEIGHT,
				"" + fontData.getHeight()); //$NON-NLS-1$
		fontElement.setAttribute(XML_ATTRIBUTE_FONT_STYLE,
				"" + fontData.getStyle()); //$NON-NLS-1$
		propElement.addContent(fontElement);
		
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		Element fontElement = propElement.getChild(XML_ELEMENT_FONT);
		
		return new FontData(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_NAME), 
				Integer.parseInt(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_HEIGHT)),
				Integer.parseInt(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_STYLE)));
	}

}
