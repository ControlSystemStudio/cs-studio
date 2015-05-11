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
 package org.csstudio.sds.ui.internal.editparts;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access service for contributions to the org.csstudio.sds.editParts.
 *
 * @author Sven Wende & Stefan Hofer
 *
 */
public final class EditPartService {
    private static final Logger LOG = LoggerFactory.getLogger(EditPartService.class);

    /**
     * The singleton instance.
     */
    private static EditPartService _instance = null;

    /**
     * Descriptors for all contributions.
     */
    private Map<String, ElementEditPartDescriptor> _descriptors;

    /**
     * Private constructor.
     */
    private EditPartService() {
        lookup();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance
     */
    public static EditPartService getInstance() {
        if (_instance == null) {
            _instance = new EditPartService();
        }

        return _instance;
    }

    /**
     * Determines, whether EditParts for model objects of the specified can be
     * created via this service or not.
     *
     * @param typeId
     *            the type identification
     * @return true, if such EditParts can be created or false, which probably
     *         means, that no EditPart contribution was registered for this type
     *         of model objects
     */
    public boolean canCreateEditPart(final String typeId) {
        return _descriptors.containsKey(typeId);
    }

    /**
     * Creates an EditPart of the specified type.
     *
     * @param typeId
     *            the type identification
     * @return an EditPart object
     */
    public AbstractGraphicalEditPart createEditPart(final String typeId) {
        assert canCreateEditPart(typeId) : "Precondition violated: hasEditPart(typeId)"; //$NON-NLS-1$
        ElementEditPartDescriptor descriptor = _descriptors.get(typeId);
        return descriptor.createEditPart();
    }

    /**
     * Looks up all extensions from the extension registry and creates the
     * corresponding descriptor objects.
     */
    private void lookup() {
        _descriptors = new HashMap<String, ElementEditPartDescriptor>();

        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        String id = SdsUiPlugin.EXTPOINT_WIDGET_EDITPARTS;
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor(id);

        for (IConfigurationElement element : confElements) {
            String typeId = element.getAttribute("typeId"); //$NON-NLS-1$

            if (_descriptors.containsKey(typeId)) {
                throw new IllegalArgumentException(
                        "Only one edit part for the type >>" + typeId //$NON-NLS-1$
                                + "<< should be registered."); //$NON-NLS-1$
            }

            if (typeId != null) {
                _descriptors.put(typeId, new ElementEditPartDescriptor(element));
            }
        }
    }

    /**
     * This descriptor for {@link AbstractWidgetEditPart}s serves as a delegate to
     * enable lazy loading of extension point implementations.
     *
     * @author Stefan Hofer
     */
    class ElementEditPartDescriptor {
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
        public ElementEditPartDescriptor(final IConfigurationElement configurationElement) {
            assert configurationElement != null;
            _configurationElement = configurationElement;
        }

        /**
         * Returns a new instance of the EditPart.
         *
         * @return a new edit part
         */
        public AbstractGraphicalEditPart createEditPart() {
            AbstractGraphicalEditPart editPart = null;
            try {
                editPart = (AbstractGraphicalEditPart) _configurationElement
                        .createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                LOG.error(e.toString());
            }
            return editPart;
        }
    }

}
