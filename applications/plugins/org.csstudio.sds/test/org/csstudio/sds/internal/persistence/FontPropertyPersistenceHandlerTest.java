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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.internal.model.FontProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link FontPropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.4 $
 *
 */
public final class FontPropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        FontPropertyPersistenceHandler handler = new FontPropertyPersistenceHandler();
        WidgetProperty property = new FontProperty(
                "description", WidgetPropertyCategory.BEHAVIOR, ColorAndFontUtil.toFontString("Arial", 11)); //$NON-NLS-1$ //$NON-NLS-2$
        property.setDynamicsDescriptor(new DynamicsDescriptor());
        property.setPropertyValue("Arial 8"); //$NON-NLS-1$

        Element valueTag = new Element("value"); //$NON-NLS-1$

        handler.writeProperty(valueTag, property.getPropertyValue());

        assertEquals(1, valueTag.getChildren().size());

        Element fontTag = valueTag.getChild("font"); //$NON-NLS-1$

        assertNotNull(fontTag);

        assertEquals(1, fontTag.getAttributes().size());

        String fontName = fontTag.getAttributeValue("font"); //$NON-NLS-1$

        assertEquals("Arial 8", fontName); //$NON-NLS-1$
    }

    /**
     * Test for the reading behaviour.
     */
    @Test
    public void testReadProperty() {
        FontPropertyPersistenceHandler handler = new FontPropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        Element colorTag = new Element(
                FontPropertyPersistenceHandler.XML_ELEMENT_FONT);
        propertyTag.addContent(colorTag);

        colorTag
                .setAttribute(
                        FontPropertyPersistenceHandler.XML_ATTRIBUTE_FONT_NAME,
                        "Arial"); //$NON-NLS-1$
        colorTag.setAttribute(
                FontPropertyPersistenceHandler.XML_ATTRIBUTE_FONT_STYLE, "0"); //$NON-NLS-1$
        colorTag.setAttribute(
                FontPropertyPersistenceHandler.XML_ATTRIBUTE_FONT_HEIGHT, "8"); //$NON-NLS-1$

        Object propertyValue = handler.readProperty(propertyTag);

        assertTrue(propertyValue instanceof String);

        assertEquals("Arial, 8", propertyValue); //$NON-NLS-1$
    }
}
