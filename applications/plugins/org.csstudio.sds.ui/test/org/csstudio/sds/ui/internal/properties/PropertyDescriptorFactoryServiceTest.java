/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.ui.internal.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * @version $Revision: 1.2 $
 *
 */
public class PropertyDescriptorFactoryServiceTest {

    /**
     * The configuration elements for extension point
     * <code>org.csstudio.sds.ui.propertyDescriptorFactories</code>.
     */
    private IConfigurationElement[] _confElements;

    /**
     * Test method for
     * {@link org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService#getInstance()}.
     */
    @Test
    public final void testGetInstance() {
        assertNotNull(PropertyDescriptorFactoryService.getInstance());
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService#hasPropertyDescriptorFactory(PropertyTypesEnum)}.
     */
    @Test
    public final void testHasPropertyDescriptorFactory() {
        for (IConfigurationElement element : _confElements) {
            String typeIdString = element.getAttribute("typeId"); //$NON-NLS-1$

            PropertyTypesEnum typeId;
            try {
                typeId = PropertyTypesEnum.createFromPortable(typeIdString);
            } catch (Exception e) {
                // apply String as default
                typeId = PropertyTypesEnum.STRING;
            }

            assertTrue(PropertyDescriptorFactoryService.getInstance()
                    .hasPropertyDescriptorFactory(typeId));
        }
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService#getPropertyDescriptorFactory(PropertyTypesEnum)}.
     */
    @Test
    public final void testGetPropertyDescriptorFactory() {
        for (IConfigurationElement element : _confElements) {
            String typeIdString = element.getAttribute("typeId"); //$NON-NLS-1$

            PropertyTypesEnum typeId;
            try {
                typeId = PropertyTypesEnum.createFromPortable(typeIdString);
            } catch (Exception e) {
                // apply String as default
                typeId = PropertyTypesEnum.STRING;
            }
            assertNotNull(PropertyDescriptorFactoryService.getInstance()
                    .getPropertyDescriptorFactory(typeId));
        }
    }

    /**
     * Read extension point registry.
     */
    @Before
    public final void setUp() {
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        String id = SdsUiPlugin.EXTPOINT_PROPERTY_DESRIPTORS_FACTORIES;
        _confElements = extReg.getConfigurationElementsFor(id);
    }

}
