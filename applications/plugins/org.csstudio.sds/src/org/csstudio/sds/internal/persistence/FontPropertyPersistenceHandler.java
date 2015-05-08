/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.persistence;

import org.csstudio.sds.util.ColorAndFontUtil;
import org.jdom.Element;

/**
 * Persistence handler for the property type <code>sds.font</code>.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class FontPropertyPersistenceHandler extends AbstractPropertyPersistenceHandler {

    private static int ITALIC = 1 << 1;
    private static int BOLD = 1 << 0;

    /**
     * XML attribute name <code>font</code>.
     */
    public static final String XML_ELEMENT_FONT = "font"; //$NON-NLS-1$

    /**
     * XML attribute name <code>fontName</code>.
     */
    public static final String XML_ATTRIBUTE_FONT = "font"; //$NON-NLS-1$

    /**
     * XML attribute name <code>fontName</code>.
     */
    @Deprecated
    public static final String XML_ATTRIBUTE_FONT_NAME = "fontName"; //$NON-NLS-1$

    /**
     * XML attribute name <code>fontName</code>.
     */
    @Deprecated
    public static final String XML_ATTRIBUTE_FONT_HEIGHT = "height"; //$NON-NLS-1$

    /**
     * XML attribute name <code>fontName</code>.
     */
    @Deprecated
    public static final String XML_ATTRIBUTE_FONT_STYLE = "style"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeProperty(final Element domElement, final Object propertyValue) {
        Element fontElement = new Element(XML_ELEMENT_FONT);

        String font = (String) propertyValue;
        fontElement.setAttribute(XML_ATTRIBUTE_FONT, font);
        domElement.addContent(fontElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readProperty(final Element domElement) {
        Element fontElement = domElement.getChild(XML_ELEMENT_FONT);

        // .. try to get the composite font string
        String font = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT);

        // .. if its not available, try it the old way (before SDS 2.0)
        if (font == null || font.length() <= 0) {
            String nameAttribute = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_NAME);
            String heightAttribute = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_HEIGHT);
            String styleAttribute = fontElement.getAttributeValue(XML_ATTRIBUTE_FONT_STYLE);

            try {
                // .. name
                String name = (nameAttribute != null && nameAttribute.length() > 0) ? nameAttribute : "Arial";

                // .. size
                int height = -1;
                try {
                    height = Integer.parseInt(heightAttribute);
                } catch (NumberFormatException nfe) {
                }

                // .. styles (bold, italic)
                int style = 0;
                try {
                    style = Integer.parseInt(styleAttribute);
                } catch (NumberFormatException nfe) {
                }

                boolean italic = ((style & ITALIC) == ITALIC);
                boolean bold = ((style & BOLD) == BOLD);

                font = ColorAndFontUtil.toFontString(name, height, bold, italic);
            } catch (Exception e) {
                font = ColorAndFontUtil.toFontString("Arial", 8);
            }

        }
        return font;
    }

}
