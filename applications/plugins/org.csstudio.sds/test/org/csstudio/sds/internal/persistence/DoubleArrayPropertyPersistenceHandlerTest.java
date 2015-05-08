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

import org.csstudio.sds.internal.model.DoubleArrayProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link DoubleArrayPropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class DoubleArrayPropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        DoubleArrayPropertyPersistenceHandler handler = new DoubleArrayPropertyPersistenceHandler();
        WidgetProperty property = new DoubleArrayProperty(
                "description", WidgetPropertyCategory.BEHAVIOR, new double[] {});//$NON-NLS-1$
        property.setDynamicsDescriptor(new DynamicsDescriptor());
        property.setPropertyValue(new double[] { 1.0, 2.0, 3.0, 4.0 });

        Element valueTag = new Element("value"); //$NON-NLS-1$

        handler.writeProperty(valueTag, property.getPropertyValue());

        assertEquals(1, valueTag.getChildren().size());

        Element doubleArrayElement = (Element) valueTag.getChildren().get(0);
        assertEquals("doubleArray", doubleArrayElement.getName()); //$NON-NLS-1$

        assertEquals(4, doubleArrayElement.getChildren().size());

        Element doubleTag = (Element) doubleArrayElement.getChildren().get(0);
        assertEquals("double", doubleTag.getName()); //$NON-NLS-1$
        assertNotNull(doubleTag);
        assertEquals(1, doubleTag.getAttributes().size());
        String valueString = doubleTag.getAttributeValue("value"); //$NON-NLS-1$
        assertEquals("1.0", valueString); //$NON-NLS-1$

        doubleTag = (Element) doubleArrayElement.getChildren().get(1);
        assertEquals("double", doubleTag.getName()); //$NON-NLS-1$
        assertNotNull(doubleTag);
        assertEquals(1, doubleTag.getAttributes().size());
        valueString = doubleTag.getAttributeValue("value"); //$NON-NLS-1$
        assertEquals("2.0", valueString); //$NON-NLS-1$

        doubleTag = (Element) doubleArrayElement.getChildren().get(2);
        assertEquals("double", doubleTag.getName()); //$NON-NLS-1$
        assertNotNull(doubleTag);
        assertEquals(1, doubleTag.getAttributes().size());
        valueString = doubleTag.getAttributeValue("value"); //$NON-NLS-1$
        assertEquals("3.0", valueString); //$NON-NLS-1$

        doubleTag = (Element) doubleArrayElement.getChildren().get(3);
        assertEquals("double", doubleTag.getName()); //$NON-NLS-1$
        assertNotNull(doubleTag);
        assertEquals(1, doubleTag.getAttributes().size());
        valueString = doubleTag.getAttributeValue("value"); //$NON-NLS-1$
        assertEquals("4.0", valueString); //$NON-NLS-1$
    }

    /**
     * Test for the reading behaviour.
     */
    @Test
    public void testReadProperty() {
        DoubleArrayPropertyPersistenceHandler handler = new DoubleArrayPropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        Element arrayTag = new Element(
                DoubleArrayPropertyPersistenceHandler.XML_ELEMENT_DOUBLE_ARRAY);
        propertyTag.addContent(arrayTag);

        arrayTag.addContent(new Element(
                DoubleArrayPropertyPersistenceHandler.XML_ELEMENT_DOUBLE)
                .setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "1.0")); //$NON-NLS-1$
        arrayTag.addContent(new Element(
                DoubleArrayPropertyPersistenceHandler.XML_ELEMENT_DOUBLE)
                .setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "2.0")); //$NON-NLS-1$
        arrayTag.addContent(new Element(
                DoubleArrayPropertyPersistenceHandler.XML_ELEMENT_DOUBLE)
                .setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "3.0")); //$NON-NLS-1$
        arrayTag.addContent(new Element(
                DoubleArrayPropertyPersistenceHandler.XML_ELEMENT_DOUBLE)
                .setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "4.0")); //$NON-NLS-1$

        Object propertyValue = handler.readProperty(propertyTag);

        assertTrue(propertyValue instanceof double[]);

        double[] doubleArray = (double[]) propertyValue;

        assertEquals(4, doubleArray.length);
        assertEquals(1.0, doubleArray[0], 0.001);
        assertEquals(2.0, doubleArray[1], 0.001);
        assertEquals(3.0, doubleArray[2], 0.001);
        assertEquals(4.0, doubleArray[3], 0.001);
    }
}
