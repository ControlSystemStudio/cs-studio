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

import org.csstudio.sds.internal.model.ColorProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link ColorPropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class ColorPropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        ColorPropertyPersistenceHandler handler = new ColorPropertyPersistenceHandler();
        WidgetProperty property = new ColorProperty(
                "description", WidgetPropertyCategory.BEHAVIOR, "#ff00ff");//$NON-NLS-1$
        property.setDynamicsDescriptor(new DynamicsDescriptor());
        property.setPropertyValue("#ff00ff");

        Element valueTag = new Element("value"); //$NON-NLS-1$

        handler.writeProperty(valueTag, property.getPropertyValue());

        assertEquals(1, valueTag.getChildren().size());

        Element colorTag = valueTag.getChild("color"); //$NON-NLS-1$

        assertNotNull(colorTag);

        assertEquals(1, colorTag.getAttributes().size());

        String hex = colorTag.getAttributeValue(ColorPropertyPersistenceHandler.XML_ATTRIBUTE_HEX); //$NON-NLS-1$

        assertEquals("#ff00ff", hex); //$NON-NLS-1$
    }

    /**
     * Test for the reading behaviour (when R/G/B attributes are set - which is the case with
     * all older SDS-files before SDS 2.0).
     */
    @Test
    public void testReadPropertyWithRgbAttributesSet() {
        ColorPropertyPersistenceHandler handler = new ColorPropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        Element colorTag = new Element(
                ColorPropertyPersistenceHandler.XML_ELEMENT_COLOR);
        propertyTag.addContent(colorTag);

        colorTag.setAttribute(
                ColorPropertyPersistenceHandler.XML_ATTRIBUTE_RED, "50"); //$NON-NLS-1$
        colorTag.setAttribute(
                ColorPropertyPersistenceHandler.XML_ATTRIBUTE_GREEN, "100"); //$NON-NLS-1$
        colorTag.setAttribute(
                ColorPropertyPersistenceHandler.XML_ATTRIBUTE_BLUE, "150"); //$NON-NLS-1$

        Object propertyValue = handler.readProperty(propertyTag);

        assertTrue(propertyValue instanceof String);

        String hex = (String) propertyValue;

        assertTrue("#326496".equalsIgnoreCase(hex));
    }

    @Test
    public void testReadPropertyWithHexAttributeSet() {
        ColorPropertyPersistenceHandler handler = new ColorPropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        Element colorTag = new Element(
                ColorPropertyPersistenceHandler.XML_ELEMENT_COLOR);
        propertyTag.addContent(colorTag);

        colorTag.setAttribute(
                ColorPropertyPersistenceHandler.XML_ATTRIBUTE_HEX, "#ff12ff"); //$NON-NLS-1$

        Object propertyValue = handler.readProperty(propertyTag);

        assertTrue(propertyValue instanceof String);

        String hex = (String) propertyValue;

        assertTrue("#ff12ff".equalsIgnoreCase(hex));
    }
}
