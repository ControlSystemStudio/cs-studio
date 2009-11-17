
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.OPIFontPropertyDescriptor;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIFont;
import org.eclipse.swt.graphics.FontData;
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
	public static final String XML_ELEMENT_FONT = "fontdata"; //$NON-NLS-1$

	
	/**
	 * XML attribute name <code>fontName</code>.
	 */
	public static final String XML_ELEMENT_FONTNAME= "opifont.name"; //$NON-NLS-1$
	
	
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
			WidgetPropertyCategory category, FontData defaultValue) {
		super(prop_id, description, category, new OPIFont(defaultValue));
	}
	
	public FontProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		super(prop_id, description, category, MediaService.getInstance().getOPIFont(defaultValue));
	}
	
	

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#checkValue(java.lang.Object)
	 */
	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		
		Object acceptedValue = value;
		
		if(value instanceof OPIFont){
			if(((OPIFont)value).getFontData() == null)
				acceptedValue = null;
		}else if (value instanceof FontData) {
			acceptedValue = new OPIFont((FontData)value);
		}else if(value instanceof String){
			acceptedValue = MediaService.getInstance().getOPIFont((String)value);
		}else
			acceptedValue = null;
		
		return acceptedValue;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#createPropertyDescriptor()
	 */
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new OPIFontPropertyDescriptor(prop_id, description);		
	}

	@Override
	public void writeToXML(Element propElement) {
		OPIFont opiFont = (OPIFont)getPropertyValue();
		
		Element fontElement;
		
		if(!opiFont.isPreDefined()){
			fontElement= new Element(XML_ELEMENT_FONT);
			FontData fontData = opiFont.getFontData();
			fontElement.setAttribute(XML_ATTRIBUTE_FONT_NAME, fontData.getName());
			fontElement.setAttribute(XML_ATTRIBUTE_FONT_HEIGHT,
					"" + fontData.getHeight()); //$NON-NLS-1$
			fontElement.setAttribute(XML_ATTRIBUTE_FONT_STYLE,
					"" + fontData.getStyle()); //$NON-NLS-1$
		}else{
			fontElement = new Element(XML_ELEMENT_FONTNAME);
			fontElement.setText(opiFont.getFontName());
		}
		
		propElement.addContent(fontElement);		
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		Element fontElement = propElement.getChild(XML_ELEMENT_FONT);
		if(fontElement !=null){
			return new OPIFont(new FontData(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_NAME), 
				Integer.parseInt(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_HEIGHT)),
				Integer.parseInt(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_STYLE))));
		}else{
			fontElement = propElement.getChild(XML_ELEMENT_FONTNAME);
			if(fontElement != null)
				return MediaService.getInstance().getOPIFont(fontElement.getText());
			else
				return null;
		}
	}

}
