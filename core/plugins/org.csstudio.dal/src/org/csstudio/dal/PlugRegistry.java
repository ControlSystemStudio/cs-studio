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
package org.csstudio.dal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.csstudio.dal.spi.Plugs;
import org.csstudio.dal.spi.PropertyFactoryService;

/**
 * This registry provides access to the DAL plugs that are registered using the
 * <code>plugs</code> extension point.
 * 
 * @author Alexander Will, Igor Kriznar
 * @version $Revision: 1.1 $
 * 
 */
public final class PlugRegistry {
	/**
	 * The shared instance of this class.
	 */
	private static PlugRegistry _instance = null;

	/**
	 * The registered DAL plugs.
	 */
	private Map<String, PlugDescriptor> _plugs;

	/**
	 * Private constructor due to the singleton pattern.
	 */
	private PlugRegistry() {
		_plugs = lookupExtensions();
	}

	/**
	 * Return the shared instance of this class.
	 * 
	 * @return The shared instance of this class.
	 */
	public static PlugRegistry getInstance() {
		if (_instance == null) {
			_instance = new PlugRegistry();
		}

		return _instance;
	}

	/**
	 * Configure all registered DAL plugs and store the created settings in the
	 * given properties object.
	 * 
	 * @param p
	 *            A properties object.
	 */
	public void configurePlugs(final Properties p) {
		// the simulator plug should be always there!
		Plugs.configureSimulatorPlug(p);

		String[] s = Plugs.getPlugNames(p);
		Set<String> set = new HashSet<String>(Arrays.asList(s));

		for (PlugDescriptor d : _plugs.values()) {
			if (!set.contains(d.getPlugId())) {
				set.add(d.getPlugId());

				StringBuffer sb = new StringBuffer();

				for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
					if (sb.length() > 0) {
						sb.append(',');
					}

					sb.append(iter.next());
				}

				p.put(Plugs.PLUGS, sb.toString());
			}

			p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + d.getPlugId(), d
					.getPropertyFactoryClass());
		}
	}
	
	public PropertyFactoryService getPropertyFactoryService(String plugId) {
		
		PlugDescriptor pd=null;
		
		if (plugId!=null) {
			pd= _plugs.get(plugId);
		}
		
		if (pd!=null) {
			return pd.propertyFactoryService;
		}
		
		return null;
		
	}

	/**
	 * Check whether the given plug name is known to this registry.
	 * 
	 * @param plugName
	 *            The plug name.
	 * @return True, if the given plug name is known to this registry.
	 */
	public boolean isRegistered(final String plugName) {
		return _plugs == null ? false : _plugs.containsKey(plugName);
	}

	/**
	 * Perform a lookup for plugins that provide extensions for the
	 * <code>plugs</code> extension point.
	 */
	private Map<String, PlugDescriptor> lookupExtensions() {
		Map<String, PlugDescriptor> descriptors = new HashMap<String, PlugDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = DalPlugin.EXTPOINT_PLUGS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			String plugId = element.getAttribute("id"); //$NON-NLS-1$
			String propertyFactoryClass = element
					.getAttribute("propertyFactoryClass"); //$NON-NLS-1$

			Object o=null;
			try {
				o = element.createExecutableExtension("propertyFactoryService");
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			if (!(o instanceof PropertyFactoryService)) {
				o=null;
			}
			
			if (plugId != null) {
				descriptors.put(plugId, 
						new PlugDescriptor(
								plugId,
								propertyFactoryClass,
								(PropertyFactoryService)o));
			}
			
		}
		
		return descriptors;
	}

	/**
	 * Descriptor for extensions of the <code>plug</code> extension points.
	 * 
	 * @author Alexander Will, Igor Kriznar
	 * 
	 */
	private class PlugDescriptor {
		/**
		 * The ID of the extension.
		 */
		private String plugId;

		/**
		 * The full qualified name of the property factory class of the plug.
		 */
		private String propertyFactoryClass;

		/**
		 * Class instance which plug defined trough plug-in estension point to be it's service.
		 */
		private PropertyFactoryService propertyFactoryService;

		/**
		 * Standard constructor.
		 * 
		 * @param plugId
		 *            The ID of the extension.
		 * @param propertyFactoryClass
		 *            The full qualified name of the property factory class of
		 *            the plug.
		 */
		public PlugDescriptor(String plugId, String propertyFactoryClass, PropertyFactoryService propertyFactoryService) {
			this.plugId = plugId;
			this.propertyFactoryClass = propertyFactoryClass;
			this.propertyFactoryService = propertyFactoryService;
		}

		/**
		 * Return the ID of the extension.
		 * 
		 * @return The ID of the extension.
		 */
		public String getPlugId() {
			return plugId;
		}

		/**
		 * Return the full qualified name of the property factory class of the
		 * plug.
		 * 
		 * @return The full qualified name of the property factory class of the
		 *         plug.
		 */
		public String getPropertyFactoryClass() {
			return propertyFactoryClass;
		}

		/**
		 * Returns property factory service implementation for plug-in.
		 * @return property factory service implementation for plug-in
		 */
		public PropertyFactoryService getPropertyFactoryService() {
			return propertyFactoryService;
		}
	}
}
