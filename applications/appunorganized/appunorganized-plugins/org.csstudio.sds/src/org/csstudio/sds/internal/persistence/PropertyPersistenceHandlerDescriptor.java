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
 package org.csstudio.sds.internal.persistence;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Descriptor for extensions of the <code>propertyPersistenceHandlers</code>
 * extension point.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class PropertyPersistenceHandlerDescriptor {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyPersistenceHandlerDescriptor.class);

    /**
     * Reference to a configuration element of the Eclipse plugin registry.
     */
    private IConfigurationElement _configurationElement;

    /**
     * A lazy instantiated property persistence handler.
     */
    private AbstractPropertyPersistenceHandler _persistenceHandler;

    /**
     * Constructs a descriptor, which is based on the specified configuration
     * element.
     *
     * @param configurationElement
     *            the configuration element
     */
    public PropertyPersistenceHandlerDescriptor(
            final IConfigurationElement configurationElement) {
        _configurationElement = configurationElement;
    }

    /**
     * Instantiate and return the persistence handler.
     *
     * @return The property persistence handler.
     */
    public AbstractPropertyPersistenceHandler getPersistenceHandler() {
        if (_persistenceHandler == null) {
            try {
                _persistenceHandler = (AbstractPropertyPersistenceHandler) _configurationElement
                        .createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                LOG.error(e.toString());
            }
        }

        return _persistenceHandler;
    }
}
