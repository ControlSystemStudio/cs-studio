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

import org.csstudio.sds.internal.model.BooleanProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link BooleanPropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class BooleanPropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        BooleanPropertyPersistenceHandler handler = new BooleanPropertyPersistenceHandler();
        WidgetProperty property = new BooleanProperty(
                "description", WidgetPropertyCategory.BEHAVIOR, false);//$NON-NLS-1$)
        property.setDynamicsDescriptor(new DynamicsDescriptor());

        property.setPropertyValue(Boolean.valueOf(true));

        Element valueTag = new Element("anyTag"); //$NON-NLS-1$

        handler.writeProperty(valueTag, property.getPropertyValue());

        assertEquals(0, valueTag.getChildren().size());

        Attribute attrib = valueTag.getAttribute("value"); //$NON-NLS-1$

        assertNotNull(attrib);
        assertEquals("true", attrib.getValue()); //$NON-NLS-1$
    }

    /**
     * Test for the reading behaviour.
     */
    @Test
    public void testReadProperty() {
        BooleanPropertyPersistenceHandler handler = new BooleanPropertyPersistenceHandler();

        Element propertyTag1 = new Element("anyTag"); //$NON-NLS-1$
        propertyTag1.setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "true"); //$NON-NLS-1$

        Element propertyTag2 = new Element("anotherTag"); //$NON-NLS-1$
        propertyTag2.setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "false"); //$NON-NLS-1$

        Element propertyTag3 = new Element("oneMoreTag"); //$NON-NLS-1$
        propertyTag3.setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "nonsense"); //$NON-NLS-1$

        Object propertyValue1 = handler.readProperty(propertyTag1);
        Object propertyValue2 = handler.readProperty(propertyTag2);
        Object propertyValue3 = handler.readProperty(propertyTag3);

        assertTrue(propertyValue1 instanceof Boolean);
        assertTrue(propertyValue2 instanceof Boolean);
        assertTrue(propertyValue3 instanceof Boolean);

        assertEquals(true, propertyValue1);
        assertEquals(false, propertyValue2);
        assertEquals(false, propertyValue3);
    }
}
