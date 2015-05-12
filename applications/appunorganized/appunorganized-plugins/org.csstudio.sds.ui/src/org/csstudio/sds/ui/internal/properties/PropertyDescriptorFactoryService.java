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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stefan Hofer
 *
 */
public final class PropertyDescriptorFactoryService {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyDescriptorFactoryService.class);

    /**
     * Instances of this class serve as delegates to
     * enable lazy loading of extension point contributions.
     *
     * @author Stefan Hofer
     */
    class PropertyDescriptorFactoryContribution {
        /**
         * The configuration element.
         */
        private final IConfigurationElement _configurationElement;

        /**
         * Constructor.
         *
         * @param configurationElement
         *            required
         */
        public PropertyDescriptorFactoryContribution(final IConfigurationElement configurationElement) {
            assert configurationElement != null;
            _configurationElement = configurationElement;
        }

        /**
         * Returns a new instance of the PropertyDescriptorFactory.
         *
         * @return A new property descriptor factory.
         */
        public IPropertyDescriptorFactory createPropertyDescriptorFactory() {
            IPropertyDescriptorFactory factory = null;
            try {
                factory = (IPropertyDescriptorFactory) _configurationElement
                        .createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                LOG.error(e.toString());
            }
            return factory;
        }
    }

    /**
     * Holds reference to the singleton instance of this class.
     */
    private static PropertyDescriptorFactoryService _instance;

    /**
     * Contributions to this extension points.
     */
    private Map<PropertyTypesEnum, PropertyDescriptorFactoryContribution> _contributions;

    /**
     * @return The singleton instance of this service.
     */
    public static PropertyDescriptorFactoryService getInstance() {
        if (_instance == null) {
            _instance = new PropertyDescriptorFactoryService();
        }
        return _instance;
    }

    /**
     * Private contructor because of singleton.
     * Use {@link PropertyDescriptorFactoryService#getInstance()}.
     */
    private PropertyDescriptorFactoryService() {
        lookup();
    }

    /**
     * Looks up all extensions from the extension registry and creates the
     * corresponding descriptor objects.
     */
    private void lookup() {
        _contributions = new HashMap<PropertyTypesEnum, PropertyDescriptorFactoryContribution>();

        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        String id = SdsUiPlugin.EXTPOINT_PROPERTY_DESRIPTORS_FACTORIES;
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor(id);

        for (IConfigurationElement element : confElements) {
            String typeIdString = element.getAttribute("typeId"); //$NON-NLS-1$


            PropertyTypesEnum typeId;
            try {
                typeId = PropertyTypesEnum
                        .createFromPortable(typeIdString);
            } catch (Exception e) {
                // apply String as default
                typeId = PropertyTypesEnum.STRING;
            }


            if (_contributions.containsKey(typeId)) {
                throw new IllegalArgumentException(
                        "Only one property descriptor factory for the type >>" + typeId //$NON-NLS-1$
                                + "<< should be registered."); //$NON-NLS-1$
            }

            if (typeId != null) {
                _contributions.put(typeId, new PropertyDescriptorFactoryContribution(element));
            }
        }
    }

    /**
     * @param typeId The typeId of the factory.
     * @return <code>true</code> if the requested factory could be found in the extension point registry.
     */
    public boolean hasPropertyDescriptorFactory(final PropertyTypesEnum typeId) {
        return _contributions.containsKey(typeId);
    }

    /**
     * @param typeId The typeId of the factory. Must be a valid factory id.
     * @return The factory corresponding to the ID.
     */
    public IPropertyDescriptorFactory getPropertyDescriptorFactory(final PropertyTypesEnum typeId) {
        assert hasPropertyDescriptorFactory(typeId) : "Precondition violated: hasPropertyDescriptor(type)"; //$NON-NLS-1$

        PropertyDescriptorFactoryContribution contribution = _contributions.get(typeId);
        return contribution.createPropertyDescriptorFactory();
    }

}
