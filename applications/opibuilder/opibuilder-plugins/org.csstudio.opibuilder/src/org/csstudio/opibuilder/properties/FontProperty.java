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
import org.csstudio.opibuilder.util.OPIFont;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * @author Sven Wende (class of same name in SDS)
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

    public static final String XML_ATTRIBUTE_FONT_PIXELS = "pixels";


    private static final String QUOTE = "\""; //$NON-NLS-1$


    /**Font Property Constructor. The property value type is {@link OPIFont}.
     * @param prop_id the property id which should be unique in a widget model.
     * @param description the description of the property,
     * which will be shown as the property name in property sheet.
     * @param category the category of the widget.
     * @param defaultValue the default value when the widget is first created.
     */
    public FontProperty(String prop_id, String description,
            WidgetPropertyCategory category, FontData defaultValue) {
        super(prop_id, description, category, MediaService.getInstance().getOPIFont(defaultValue));
    }
    /**Font Property Constructor. The property value type is {@link OPIFont}.
     * @param prop_id the property id which should be unique in a widget model.
     * @param description the description of the property,
     * which will be shown as the property name in property sheet.
     * @param category the category of the widget.
     * @param defaultValue the default value when the widget is first created.
     * It must be a exist font macro name in font file.
     */
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
            // Avoid getFontData() as this method can be called from off the UI thread.
            if(((OPIFont)value).getRawFontData() == null)
                acceptedValue = null;
        }else if (value instanceof FontData) {
            acceptedValue = MediaService.getInstance().getOPIFont((FontData)value);
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
        if(PropertySSHelper.getIMPL() == null)
            return null;
        return PropertySSHelper.getIMPL().getOPIFontPropertyDescriptor(prop_id, description);
    }

    @Override
    public void writeToXML(Element propElement) {
        OPIFont opiFont = (OPIFont)getPropertyValue();

        Element fontElement;

        if(!opiFont.isPreDefined()){
            fontElement= new Element(XML_ELEMENT_FONT);
        }else{
            fontElement = new Element(XML_ELEMENT_FONTNAME);
            fontElement.setText(opiFont.getFontMacroName());
        }
        FontData fontData = opiFont.getFontData();
        fontElement.setAttribute(XML_ATTRIBUTE_FONT_NAME, fontData.getName());
        fontElement.setAttribute(XML_ATTRIBUTE_FONT_HEIGHT,
                "" + fontData.getHeight()); //$NON-NLS-1$
        fontElement.setAttribute(XML_ATTRIBUTE_FONT_STYLE,
                "" + fontData.getStyle()); //$NON-NLS-1$
        fontElement.setAttribute(XML_ATTRIBUTE_FONT_PIXELS, "" + opiFont.isSizeInPixels());

        propElement.addContent(fontElement);
    }

    @Override
    public Object readValueFromXML(Element propElement) {
        Element fontElement = propElement.getChild(XML_ELEMENT_FONT);
        if(fontElement !=null){
            return MediaService.getInstance().getOPIFont(new FontData(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_NAME),
                (int) Double.parseDouble(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_HEIGHT)),
                Integer.parseInt(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_STYLE))),
                    Boolean.parseBoolean(fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_PIXELS))
                    );
        }else{
            fontElement = propElement.getChild(XML_ELEMENT_FONTNAME);
            if(fontElement != null){
                OPIFont font = null;
                String fontName = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_NAME);
                String fontHeight=fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_HEIGHT);
                String fontStyle = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_STYLE);
                String heightInPixels = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_PIXELS);
                if(fontName != null && fontHeight != null && fontStyle != null){
                    FontData fd = new FontData(fontName, (int) Double.parseDouble(fontHeight),
                            Integer.parseInt(fontStyle));
                    font = MediaService.getInstance().getOPIFont(fontElement.getText(), fd);
                } else {
                    font = MediaService.getInstance().getOPIFont(fontElement.getText());
                }
                // If this was serialised without a height in pixels attribute, it was from
                // an older verison of BOY where points were assumed.  To ensure the screens are not
                // changed when re-saved, make this explicit.
                if (heightInPixels != null) {
                    boolean inPixels = Boolean.parseBoolean(heightInPixels);
                    font.setSizeInPixels(inPixels);
                } else {
                    font.setSizeInPixels(false);
                }
                return font;
            }
            else
                return null;
        }
    }

    @Override
    public boolean configurableByRule() {
        return true;
    }

    @Override
    public String toStringInRuleScript(Object propValue) {
        OPIFont opiFont = (OPIFont)propValue;
        if(opiFont.isPreDefined())
            return QUOTE + opiFont.getFontMacroName() + QUOTE;
        else{
            FontData fontData = opiFont.getFontData();
            return "ColorFontUtil.getFont(\"" +
                fontData.getName() + QUOTE + "," + fontData.getHeight() + "," + fontData.getStyle() + ")";
        }
    }



}
