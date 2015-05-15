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
package org.csstudio.sds.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IWidgetModelFactory;
import org.junit.Test;

/**
 * Test case for class {@link WidgetModelFactoryDescriptor}.
 *
 * @author Alexander Will
 * @version $Revision: 1.4 $
 *
 */
public final class WidgetModelFactoryDescriptorTest {

    /**
     * Test method for
     * {@link org.csstudio.sds.internal.model.WidgetModelFactoryDescriptor}.
     */
    @Test
    public void testGetDescription() {
        IWidgetModelFactory factory = new IWidgetModelFactory() {
            public AbstractWidgetModel createWidgetModel() {
                return null;
            }

            @SuppressWarnings("unchecked")
            public Class getWidgetModelType() {
                return AbstractWidgetModel.class;
            }
        };

        WidgetModelFactoryDescriptor descriptor = new WidgetModelFactoryDescriptor(
                "description", "name", "icon", factory, "pluginId"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        assertEquals("description", descriptor.getDescription()); //$NON-NLS-1$
        assertEquals("name", descriptor.getName()); //$NON-NLS-1$
        assertEquals("icon", descriptor.getIcon()); //$NON-NLS-1$
        assertEquals("pluginId", descriptor.getPluginId()); //$NON-NLS-1$

        assertEquals(factory, descriptor.getFactory());
        assertNull(descriptor.getFactory().createWidgetModel());
        assertEquals(AbstractWidgetModel.class, descriptor.getFactory()
                .getWidgetModelType());
    }

}
