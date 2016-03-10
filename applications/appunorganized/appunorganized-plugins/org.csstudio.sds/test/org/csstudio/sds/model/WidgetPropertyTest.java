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
package org.csstudio.sds.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for class {@link WidgetProperty}.
 *
 * @author Alexander Will
 * @version $Revision: 1.7 $
 *
 */
public final class WidgetPropertyTest implements IPropertyChangeListener {

    /**
     * Test method for {@link org.csstudio.sds.model.WidgetProperty}.
     */
    @Test
    public void testElementProperty() {
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();

        String defaultValue = "default";
        WidgetPropertyCategory category = WidgetPropertyCategory.BEHAVIOR;
        String name = "description";
        PropertyTypesEnum type = PropertyTypesEnum.STRING;

        WidgetProperty ep = new WidgetProperty(type, name, category,
                defaultValue, dynamicsDescriptor) {
            /**
             * {@inheritDoc}
             */
            @Override
            public Object checkValue(final Object value) {
                return value;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            @SuppressWarnings("unchecked")
            public Class[] getCompatibleJavaTypes() {
                return new Class[] {Object.class };
            }

        };

        assertEquals(name, ep.getDescription());
        assertEquals(defaultValue, ep.getDefaultValue());
        assertEquals(defaultValue, ep.getPropertyValue());
        assertEquals(type, ep.getPropertyType());
        assertEquals(category, ep.getCategory());
        assertEquals(ep.getDynamicsDescriptor(), dynamicsDescriptor);
        ep.setPropertyValue("new value"); //$NON-NLS-1$
        assertEquals(defaultValue, ep.getDefaultValue());
        assertEquals("new value", ep.getPropertyValue()); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dynamicsDescriptorChanged(
            final DynamicsDescriptor dynamicsDescriptor) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyManualValueChanged(String propertyId, final Object manualValue) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyValueChanged(final Object oldValue,
            final Object newValue) {

    }

}
