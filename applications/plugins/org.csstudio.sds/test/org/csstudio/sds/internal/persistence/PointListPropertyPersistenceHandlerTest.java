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

import org.csstudio.sds.internal.model.PointlistProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.eclipse.draw2d.geometry.PointList;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link PointListPropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class PointListPropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        PointListPropertyPersistenceHandler handler = new PointListPropertyPersistenceHandler();
        WidgetProperty property = new PointlistProperty(
                "description", WidgetPropertyCategory.POSITION, new PointList());//$NON-NLS-1$
        property.setDynamicsDescriptor(new DynamicsDescriptor());
        property.setPropertyValue(new PointList(new int[] { 1, 2, 3, 4 }));

        Element valueTag = new Element("value"); //$NON-NLS-1$

        handler.writeProperty(valueTag, property.getPropertyValue());

        assertEquals(1, valueTag.getChildren().size());

        Element pointListElement = (Element) valueTag.getChildren().get(0);
        assertEquals("pointList", pointListElement.getName()); //$NON-NLS-1$

        assertEquals(2, pointListElement.getChildren().size());

        Element pointTag = (Element) pointListElement.getChildren().get(0);
        assertEquals("point", pointTag.getName()); //$NON-NLS-1$

        assertNotNull(pointTag);

        assertEquals(2, pointTag.getAttributes().size());

        String xString = pointTag.getAttributeValue("x"); //$NON-NLS-1$
        String yString = pointTag.getAttributeValue("y"); //$NON-NLS-1$

        assertEquals("1", xString); //$NON-NLS-1$
        assertEquals("2", yString); //$NON-NLS-1$

        pointTag = (Element) pointListElement.getChildren().get(1);
        assertEquals("point", pointTag.getName()); //$NON-NLS-1$

        assertNotNull(pointTag);

        assertEquals(2, pointTag.getAttributes().size());

        xString = pointTag.getAttributeValue("x"); //$NON-NLS-1$
        yString = pointTag.getAttributeValue("y"); //$NON-NLS-1$

        assertEquals("3", xString); //$NON-NLS-1$
        assertEquals("4", yString); //$NON-NLS-1$
    }

    /**
     * Test for the reading behaviour.
     */
    @Test
    public void testReadProperty() {
        PointListPropertyPersistenceHandler handler = new PointListPropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        Element pointListTag = new Element(
                PointListPropertyPersistenceHandler.XML_ELEMENT_POINT_LIST);
        propertyTag.addContent(pointListTag);

        Element point1 = new Element(
                PointListPropertyPersistenceHandler.XML_ELEMENT_POINT);
        point1.setAttribute(
                PointListPropertyPersistenceHandler.XML_ATTRIBUTE_X, "10"); //$NON-NLS-1$
        point1.setAttribute(
                PointListPropertyPersistenceHandler.XML_ATTRIBUTE_Y, "20"); //$NON-NLS-1$
        pointListTag.addContent(point1);

        Element point2 = new Element(
                PointListPropertyPersistenceHandler.XML_ELEMENT_POINT);
        point2.setAttribute(
                PointListPropertyPersistenceHandler.XML_ATTRIBUTE_X, "100"); //$NON-NLS-1$
        point2.setAttribute(
                PointListPropertyPersistenceHandler.XML_ATTRIBUTE_Y, "200"); //$NON-NLS-1$
        pointListTag.addContent(point2);

        Object propertyValue = handler.readProperty(propertyTag);
        assertTrue(propertyValue instanceof PointList);

        PointList pointList = (PointList) propertyValue;
        assertEquals(2, pointList.size());
        assertEquals(10, pointList.getPoint(0).x);
        assertEquals(20, pointList.getPoint(0).y);
        assertEquals(100, pointList.getPoint(1).x);
        assertEquals(200, pointList.getPoint(1).y);
    }

}
