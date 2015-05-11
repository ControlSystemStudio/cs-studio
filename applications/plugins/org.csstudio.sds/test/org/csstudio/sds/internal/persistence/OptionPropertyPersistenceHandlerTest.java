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

import org.csstudio.sds.internal.model.ArrayOptionProperty;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.jdom.Element;
import org.junit.Test;

/**
 * Test case for class {@link ArrayOptionPropertyPersistenceHandler}.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class OptionPropertyPersistenceHandlerTest {
    /**
     * Test for the writing behaviour.
     */
    @Test
    public void testWriteProperty() {
        ArrayOptionPropertyPersistenceHandler handler = new ArrayOptionPropertyPersistenceHandler();
        WidgetProperty property = new ArrayOptionProperty(
                "description", WidgetPropertyCategory.BEHAVIOR, new String[] { "Option1", "Option2", "Option3" }, 1);//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        property.setDynamicsDescriptor(new DynamicsDescriptor());
        property.setPropertyValue(2);

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$

        handler.writeProperty(propertyTag, property.getPropertyValue());
        assertEquals(1, propertyTag.getChildren().size());

        Element optionElement = (Element) propertyTag.getChildren().get(0);
        assertNotNull(optionElement);
        assertEquals(0, optionElement.getChildren().size());

        assertEquals(
                "2", //$NON-NLS-1$
                optionElement
                        .getAttributeValue(ArrayOptionPropertyPersistenceHandler.XML_ATTRIBUTE_OPTION_ID));
    }

    /**
     * Test for the reading behaviour.
     */
    @Test
    public void testReadProperty() {
        ArrayOptionPropertyPersistenceHandler handler = new ArrayOptionPropertyPersistenceHandler();

        Element propertyTag = new Element("anyTag"); //$NON-NLS-1$
        Element optionElement = new Element(
                ArrayOptionPropertyPersistenceHandler.XML_ELEMENT_OPTION);
        optionElement.setAttribute(
                ArrayOptionPropertyPersistenceHandler.XML_ATTRIBUTE_OPTION_ID, "2"); //$NON-NLS-1$
        propertyTag.addContent(optionElement);

        Object propertyValue = handler.readProperty(propertyTag);

        assertTrue(propertyValue instanceof Integer);

        assertEquals(2, propertyValue);
    }

}
