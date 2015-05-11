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
package org.csstudio.sds.ui.internal.editor.newproperties.colorservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * SAX handler for colors and fonts. <br>
 *
 * @author Kai Meyer (C1 WPS)
 * @version $Revision: 1.2 $
 *
 */
public final class ColorAndFontSaxHandler extends AbstractColorAndFontHandler {

    private Map<String, NamedStyle> _styles;

    private NamedStyle _currentStyle;

    /**
     * Standard constructor.
     */
    public ColorAndFontSaxHandler() {
        _styles = new HashMap<String, NamedStyle>();
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        reset();
    }

    public void reset() {
        _styles.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String qName, final Attributes attributes)
            throws SAXException {
        if (ColorAndFontConstants.STYLE_TAG.getIdentifier().equals(qName)) {
            NamedStyle style = createStyle(attributes);
            _styles.put(style.getName(), style);
            _currentStyle = style;
        } else if (ColorAndFontConstants.COLOR_TAG.getIdentifier().equals(qName)) {
            if (_currentStyle != null) {
                _currentStyle.addColor(createColor(attributes));
            }
        } else if (ColorAndFontConstants.FONT_TAG.getIdentifier().equals(qName)) {
            if (_currentStyle != null) {
                _currentStyle.addFont(createFont(attributes));
            }
        }
    }

    private NamedStyle createStyle(Attributes attributes) throws SAXException {
        try {
            String name = attributes.getValue(ColorAndFontConstants.NAME_TAG.getIdentifier());
            String description = attributes.getValue(ColorAndFontConstants.DESCRIPTION_TAG.getIdentifier());
            NamedStyle result = new NamedStyle(name, description);
            return result;
        } catch (Exception e) {
            throw new SAXException("Couldn't create Style", e);
        }
    }

    private NamedColor createColor(Attributes attributes) throws SAXException {
        try {
            String name = attributes.getValue(ColorAndFontConstants.NAME_TAG.getIdentifier());
            String description = attributes.getValue(ColorAndFontConstants.DESCRIPTION_TAG.getIdentifier());
            String hex = attributes.getValue(ColorAndFontConstants.COLOR_HEX_TAG.getIdentifier());
            NamedColor result = new NamedColor(name, description, hex);
            return result;
        } catch (Exception e) {
            throw new SAXException("Couldn't create Color", e);
        }
    }

    private NamedFont createFont(Attributes attributes) throws SAXException {
        try {
            String name = attributes.getValue(ColorAndFontConstants.NAME_TAG.getIdentifier());
            String description = attributes.getValue(ColorAndFontConstants.DESCRIPTION_TAG.getIdentifier());
            String fontName = attributes.getValue(ColorAndFontConstants.FONT_NAME_TAG.getIdentifier());
            String fontSizeString = attributes.getValue(ColorAndFontConstants.FONT_SIZE_TAG.getIdentifier());
            int fontSize = Integer.valueOf(fontSizeString);
            String boldString = attributes.getValue(ColorAndFontConstants.FONT_BOLD_TAG.getIdentifier());
            boolean bold = Boolean.valueOf(boldString);
            String italicString = attributes.getValue(ColorAndFontConstants.FONT_ITALIC_TAG.getIdentifier());
            boolean italic = Boolean.valueOf(italicString);
            NamedFont result = new NamedFont(name, description, fontName, fontSize, bold, italic);
            return result;
        } catch (Exception e) {
            throw new SAXException("Couldn't create font", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName,
            final String qName) throws SAXException {
        if (ColorAndFontConstants.STYLE_TAG.getIdentifier().equals(qName)) {
            _currentStyle = null;
        }
    }

    public NamedStyle getStyle(String styleName) {
        return _styles.get(styleName);
    }

    public List<NamedStyle> getStyles() {
        return new ArrayList<NamedStyle>(_styles.values());
    }

}
