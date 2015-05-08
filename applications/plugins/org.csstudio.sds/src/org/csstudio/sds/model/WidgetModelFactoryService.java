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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.WidgetModelFactoryDescriptor;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.internal.preferences.WidgetSelectionStringConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * This class provides access to the <code>widgetModelFactories</code> extension
 * point.
 *
 * @author Alexander Will
 * @version $Revision: 1.8 $
 *
 */
public final class WidgetModelFactoryService {
    /**
     * The shared instance of this class.
     */
    private static WidgetModelFactoryService _instance = null;

    private static final String DEFAULT_CATEGORY = "Others";

    /**
     * The descriptors of all registered extensions.
     */
    private Map<String, WidgetModelFactoryDescriptor> _allDescriptors = null;

    private HashMap<String, List<String>> _allCategories;

    /**
     * Private constructor due to the singleton pattern.
     */
    private WidgetModelFactoryService() {
        lookup();
    }

    /**
     * Return the widget type IDs of all registered widget models.
     *
     * @return The widget type IDs of all registered widget models.
     */
    public Set<String> getUsedWidgetTypes() {
        Set<String> result = new HashSet<String>(_allDescriptors.keySet());
        result.removeAll(determineExcludedWidgetIds());
        return result;
    }

    /**
     * Return the shared instance of this class.
     *
     * @return The shared instance of this class.
     */
    public static WidgetModelFactoryService getInstance() {
        if (_instance == null) {
            _instance = new WidgetModelFactoryService();
        }

        return _instance;
    }

    public Set<String> getAllCategories() {
        return _allCategories.keySet();
    }

    public Set<String> getWidgetForCategory(String category) {
        Set<String> result = new HashSet<String>(_allCategories.get(category));
        result.removeAll(determineExcludedWidgetIds());
        return result;
    }

    /**
     * Creates a widget of the specified type.
     *
     * @param typeId
     *            type identifier
     *
     * @return a new widget or null
     */
    public AbstractWidgetModel getWidgetModel(final String typeId) {
        AbstractWidgetModel model = null;

        IWidgetModelFactory factory = _allDescriptors.get(typeId).getFactory();

        if (factory != null) {
            model = factory.createWidgetModel();
        }

        return model;
    }

    @SuppressWarnings("unchecked")
    public Object getWidgetModelType(String widgetType) {
        Class type = null;

        IWidgetModelFactory factory = _allDescriptors.get(widgetType).getFactory();

        if (factory != null) {
            type = factory.getWidgetModelType();
        }

        return type;
    }

    /**
     * Return the description of the widget model factory for the given type ID.
     *
     * @param typeId
     *            A widget model type ID.
     * @return The description of the widget model factory for the given type
     *         ID.
     */
    public String getDescription(final String typeId) {
        return _allDescriptors.get(typeId).getDescription();
    }

    /**
     * Return the icon ressource path of the widget model factory for the given
     * type ID.
     *
     * @param typeId
     *            A widget model type ID.
     * @return the icon ressource path of the widget model factory for the given
     *         type ID.
     */
    public String getIcon(final String typeId) {
        WidgetModelFactoryDescriptor descriptor = _allDescriptors.get(typeId);
        return descriptor != null ? descriptor.getIcon() : null;
    }

    /**
     * Return the name of the widget model factory for the given type ID.
     *
     * @param typeId
     *            A widget model type ID.
     * @return The name of the widget model factory for the given type ID.
     */
    public String getName(final String typeId) {
        return _allDescriptors.get(typeId).getName();
    }

    /**
     * Return the ID of the plugin that provides the widget model factory for
     * the given type ID.
     *
     * @param typeId
     *            A widget model type ID.
     * @return The ID of the plugin that provides the widget model factory for
     *         the given type ID.
     */
    public String getContributingPluginId(final String typeId) {
        WidgetModelFactoryDescriptor descriptor = _allDescriptors.get(typeId);
        return descriptor != null ? descriptor.getPluginId() : null;
    }

    /**
     * Returns the IDs of all known contributing plugins.
     *
     * @return A set of the plugin-IDs
     */
    public Set<String> getAllContributingPluginIds() {
        HashSet<String> contributingPluginIds = new HashSet<String>();
        for (String typeId : _allDescriptors.keySet()) {
            contributingPluginIds.add(this.getContributingPluginId(typeId));
        }
        return contributingPluginIds;
    }

    /**
     * Returns all widget-IDs contributed by the plugin with the given
     * <code>pluginId</code>.
     *
     * @param pluginId
     *            The ID of the plugin
     * @return A set of the widget-IDs
     */
    public Set<String> getWidgetIdsOfPlugin(final String pluginId) {
        HashSet<String> typeIds = new HashSet<String>();
        for (String typeId : _allDescriptors.keySet()) {
            if (this.getContributingPluginId(typeId).equals(pluginId)) {
                typeIds.add(typeId);
            }
        }
        return typeIds;
    }

    /**
     * Perform a lookup for plugin that provide extensions for the
     * <code>widgetModelFactories</code> extension point.
     */
    private void lookup() {
        _allDescriptors = new HashMap<String, WidgetModelFactoryDescriptor>();
        _allCategories = new HashMap<String, List<String>>();

        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        String id = SdsPlugin.EXTPOINT_WIDGET_MODEL_FACTORIES;
        IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(id);

        for (IConfigurationElement element : confElements) {
            IWidgetModelFactory factory = null;
            String typeId = element.getAttribute("typeId"); //$NON-NLS-1$
            String name = element.getAttribute("name"); //$NON-NLS-1$
            String description = element.getAttribute("description"); //$NON-NLS-1$
            String icon = element.getAttribute("icon"); //$NON-NLS-1$
            String pluginId = element.getDeclaringExtension().getNamespaceIdentifier();
            String category = element.getAttribute("category");
            if (category == null || category.trim().length() == 0) {
                category = DEFAULT_CATEGORY;
            }
            try {
                factory = (IWidgetModelFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                e.printStackTrace();
            }

            if (factory != null && typeId != null) {
                List<String> list = _allCategories.get(category);
                if (list == null) {
                    list = new ArrayList<String>();
                    _allCategories.put(category, list);
                }
                list.add(typeId);
                _allDescriptors.put(typeId, new WidgetModelFactoryDescriptor(description, name, icon, factory, pluginId));
            }
        }

    }

    /**
     * Determines the excluded Widgets based on the settings in the preference
     * page.
     *
     * @return The list of excluded widget-ids
     */
    private List<String> determineExcludedWidgetIds() {
        String excludedWidgets = Platform.getPreferencesService().getString(SdsPlugin.getDefault().getBundle().getSymbolicName(),
                PreferenceConstants.PROP_DESELECTED_WIDGETS, "", null);

        return WidgetSelectionStringConverter.createStringListFromString(excludedWidgets);
    }

}
