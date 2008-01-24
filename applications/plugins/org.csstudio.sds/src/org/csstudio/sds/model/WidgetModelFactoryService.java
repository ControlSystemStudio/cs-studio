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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.WidgetModelFactoryDescriptor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * This class provides access to the <code>widgetModelFactories</code>
 * extension point.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class WidgetModelFactoryService {
	/**
	 * The shared instance of this class.
	 */
	private static WidgetModelFactoryService _instance = null;

	/**
	 * The descriptors of all registered extensions.
	 */
	private Map<String, WidgetModelFactoryDescriptor> _descriptors = null;

	/**
	 * Private constructor due to the singleton pattern.
	 */
	private WidgetModelFactoryService() {
		lookup();
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

	/**
	 * Return the widget type IDs of all registered widget models.
	 * 
	 * @return The widget type IDs of all registered widget models.
	 */
	public Set<String> getWidgetTypes() {
		return new HashSet<String>(_descriptors.keySet());
	}

	/**
	 * Return whether a widget model factory for the given type ID is
	 * registered.
	 * 
	 * @param typeId
	 *            A widget model type ID.
	 * @return True, if a widget model factory is registered for the given type
	 *         ID.
	 */
	public boolean hasWidgetModelFactory(final String typeId) {
		return _descriptors.containsKey(typeId);
	}

	/**
	 * Return the widget model factory for the given type ID.
	 * 
	 * @param typeId
	 *            A widget model type ID.
	 * @return The widget model factory for the given type ID.
	 */
	public IWidgetModelFactory getWidgetModelFactory(final String typeId) {
		return _descriptors.get(typeId).getFactory();
	}

	/**
	 * Return the description of the widget model factory for the given type
	 * ID.
	 * 
	 * @param typeId
	 *            A widget model type ID.
	 * @return The description of the widget model factory for the given type
	 *         ID.
	 */
	public String getDescription(final String typeId) {
		return _descriptors.get(typeId).getDescription();
	}

	/**
	 * Return the icon ressource path of the widget model factory for the given
	 * type ID.
	 * 
	 * @param typeId
	 *            A widget model type ID.
	 * @return the icon ressource path of the widget model factory for the
	 *         given type ID.
	 */
	public String getIcon(final String typeId) {
		return _descriptors.get(typeId).getIcon();
	}

	/**
	 * Return the name of the widget model factory for the given type ID.
	 * 
	 * @param typeId
	 *            A widget model type ID.
	 * @return The name of the widget model factory for the given type ID.
	 */
	public String getName(final String typeId) {
		return _descriptors.get(typeId).getName();
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
		return _descriptors.get(typeId).getPluginId();
	}	

	/**
	 * Perform a lookup for plugin that provide extensions for the
	 * <code>widgetModelFactories</code> extension point.
	 */
	private void lookup() {
		_descriptors = new HashMap<String, WidgetModelFactoryDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsPlugin.EXTPOINT_WIDGET_MODEL_FACTORIES;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			IWidgetModelFactory factory = null;
			String typeId = element.getAttribute("typeId"); //$NON-NLS-1$
			String name = element.getAttribute("name"); //$NON-NLS-1$
			String description = element.getAttribute("description"); //$NON-NLS-1$
			String icon = element.getAttribute("icon"); //$NON-NLS-1$
			String pluginId = element.getDeclaringExtension()
					.getNamespaceIdentifier();
			try {
				factory = (IWidgetModelFactory) element
						.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				e.printStackTrace();
			}

			if (factory != null && typeId != null) {
				_descriptors.put(typeId, new WidgetModelFactoryDescriptor(
						description, name, icon, factory, pluginId));
			}
		}

	}

}
