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

import java.util.HashMap;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Central registry for extensions of the
 * <code>propertyPersistenceHandlers</code> extension point.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class PropertyPersistenceHandlerRegistry {
    /**
     * The singleton instance.
     */
    private static PropertyPersistenceHandlerRegistry _instance;

    /**
     * Contains the persistence handler descriptors.
     */
    private HashMap<PropertyTypesEnum, PropertyPersistenceHandlerDescriptor> _persistenceHandlers;

    /**
     * Private constructor.
     */
    private PropertyPersistenceHandlerRegistry() {
        lookup();
    }

    /**
     * @return The singleton instance.
     */
    public static PropertyPersistenceHandlerRegistry getInstance() {
        if (_instance == null) {
            _instance = new PropertyPersistenceHandlerRegistry();
        }

        return _instance;
    }

    /**
     * Internal lookup of the Eclipse extension registry.
     */
    private void lookup() {
        _persistenceHandlers = new HashMap<PropertyTypesEnum, PropertyPersistenceHandlerDescriptor>();

        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        String id = SdsPlugin.EXTPOINT_PROPERTY_PERSISTENCE_HANDLERS;
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor(id);

        for (IConfigurationElement element : confElements) {
            String typeString= element.getAttribute("typeId"); //$NON-NLS-1$


            PropertyTypesEnum typeId;
            try {
                typeId = PropertyTypesEnum.createFromPortable(typeString);
            } catch (Exception e) {
                // apply String as default
                typeId = PropertyTypesEnum.STRING;
            }

            if (_persistenceHandlers.containsKey(typeId)) {
                throw new IllegalArgumentException(
                        "Only one item factory for the type >>" + typeId //$NON-NLS-1$
                                + "<< should be registered."); //$NON-NLS-1$
            }
            _persistenceHandlers.put(typeId,
                    new PropertyPersistenceHandlerDescriptor(element));
        }
    }

    /**
     * Instantiate and return the persistence handler for the given property
     * type.
     *
     * @param typeId
     *            The property type Id.
     * @return The persistence handler for the given property type.
     */
    public AbstractPropertyPersistenceHandler getPersistenceHandler(
            final PropertyTypesEnum typeId) {
        AbstractPropertyPersistenceHandler result = null;

        PropertyPersistenceHandlerDescriptor descriptor = _persistenceHandlers
                .get(typeId);

        if (descriptor != null) {
            result = descriptor.getPersistenceHandler();
            assert result != null : "type id was valid, but no persistence handler was instantiated"; //$NON-NLS-1$
        }

        return result;
    }
}
