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

import org.csstudio.sds.internal.model.DoubleProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link DoublePropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class DoublePropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        DoublePropertyPersistenceHandler handler = new DoublePropertyPersistenceHandler();
        WidgetProperty property = new DoubleProperty(
                "description", WidgetPropertyCategory.BEHAVIOR, 0.0);//$NON-NLS-1$
        property.setDynamicsDescriptor(new DynamicsDescriptor());
        property.setPropertyValue(1.334d);

        Element valueTag = new Element("anyTag"); //$NON-NLS-1$

        handler.writeProperty(valueTag, property.getPropertyValue());

        assertEquals(0, valueTag.getChildren().size());

        Attribute attrib = valueTag.getAttribute("value"); //$NON-NLS-1$

        assertNotNull(attrib);
        assertEquals("1.334", attrib.getValue()); //$NON-NLS-1$
    }

    /**
     * Test for the reading behaviour.
     */
    @Test
    public void testReadProperty() {
        DoublePropertyPersistenceHandler handler = new DoublePropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        propertyTag.setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "1.334"); //$NON-NLS-1$

        Object propertyValue = handler.readProperty(propertyTag);

        assertTrue(propertyValue instanceof Double);

        assertEquals(1.334, propertyValue);
    }

}
