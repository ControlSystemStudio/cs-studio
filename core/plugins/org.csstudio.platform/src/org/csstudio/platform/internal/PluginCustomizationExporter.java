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
package org.csstudio.platform.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.csstudio.platform.CSSPlatformPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Utility class for exporting preferences into a file suitable for use as
 * pluing customization file. The exported file can for example be used as an
 * argument to Eclipse's -pluginCustomization command line switch.
 * 
 * @author Joerg Rathlev
 */
public final class PluginCustomizationExporter {
	
	/**
	 * The scopes to include in the export, in the order of export.
	 */
	private static final String[] SCOPES = { InstanceScope.SCOPE,
			ConfigurationScope.SCOPE };
	
	/**
	 * Stores the exported preferences.
	 */
	private Properties result = new Properties();
	
	/**
	 * <p>Exports the current preferences to the given output stream. Exports
	 * the instance and configuration scope, in a format that is suitable for
	 * use as a plugin customization file (i.e. the scope is not written into
	 * the output).</p>
	 * 
	 * <p>For example, if a plugin com.example.plugin is installed and has a
	 * preference called &quot;foo&quot; currently set to the value
	 * &quot;bar&quot; in the instance scope, the following line will be
	 * written into the output:</p>
	 * 
	 * <pre>com.example.plugin/foo=bar</pre>
	 * 
	 * <p>The output stream remains open after this method returns.</p>
	 * 
	 * @param output the output stream to write into.
	 * @throws CoreException if the export fails.
	 */
	public static void exportTo(OutputStream output) throws CoreException {
		PluginCustomizationExporter exporter = new PluginCustomizationExporter();
		try {
			exporter.export(output);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, CSSPlatformPlugin.ID,
					"Export of preferences failed.", e);
			throw new CoreException(status);
		}
	}
	
	/**
	 * Private constructor to prevent instanciation by other classes. Creating
	 * exporter objects is encapsulated in the {@link #exportTo(OutputStream)}
	 * method to prevent accidental reuse of exporter objects.
	 */
	private PluginCustomizationExporter() {
	}
	
	/**
	 * Performs the actual export.
	 * 
	 * @param out the output stream to write the result into.
	 * @throws BackingStoreException
	 * @throws IOException 
	 */
	private void export(OutputStream out) throws BackingStoreException, IOException {
		IPreferencesService prefsService = Platform.getPreferencesService();
		IEclipsePreferences root = prefsService.getRootNode();
		for (String scope : SCOPES) {
			Preferences scopeNode = root.node(scope);
			for (String child : scopeNode.childrenNames()) {
				exportNode(scopeNode.node(child), child);
			}
		}
		result.store(out, "");
	}
	
	/**
	 * Exports the given node.
	 * 
	 * @param preferenceNode the node to export.
	 * @param path the given node's path as it should be exported.
	 * @throws BackingStoreException 
	 */
	private void exportNode(Preferences preferenceNode, String path) throws BackingStoreException {
		for (String key : preferenceNode.keys()) {
			String propertyKey = path + "/" + key;
			// Only export the preference if it is not already contained in the
			// result. This is so that multiple scopes can be exported without
			// conflicting in unexpected ways.
			if (!result.containsKey(propertyKey)) {
				String propertyValue = preferenceNode.get(key, "");
				result.setProperty(propertyKey, propertyValue);
			}
		}
		
		// recursively export the node's children
		for (String child : preferenceNode.childrenNames()) {
			exportNode(preferenceNode.node(child), path + "/" + child);
		}
	}
}
