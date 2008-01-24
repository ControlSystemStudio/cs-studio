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
package org.csstudio.sds;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.ResourceService;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.internal.SdsResourceChangeListener;
import org.csstudio.sds.model.logic.RuleService;
import org.csstudio.sds.util.StringUtil;
import org.eclipse.core.resources.IProject;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Alexander Will
 * @version $Revision$
 */
public final class SdsPlugin extends AbstractCssPlugin {

	/**
	 * The ID of this plugin.
	 */
	public static final String PLUGIN_ID = "org.csstudio.sds"; //$NON-NLS-1$

	/**
	 * The ID of the rules extension point.
	 */
	public static final String EXTPOINT_RULES = PLUGIN_ID + ".rules"; //$NON-NLS-1$

	/**
	 * The ID of the widget model factories extension point.
	 */
	public static final String EXTPOINT_WIDGET_MODEL_FACTORIES = PLUGIN_ID
			+ ".widgetModelFactories"; //$NON-NLS-1$

	/**
	 * Extension point ID for the <b>propertyPersistenceHandlers</b> extension
	 * point.
	 */
	public static final String EXTPOINT_PROPERTY_PERSISTENCE_HANDLERS = PLUGIN_ID
			+ ".propertyPersistenceHandlers"; //$NON-NLS-1$	

	/**
	 * Extension point ID for the <b>widgetModelInitializers</b> extension
	 * point.
	 */
	public static final String EXTPOINT_WIDGET_MODEL_INITIALIZERS = PLUGIN_ID
			+ ".widgetModelInitializers"; //$NON-NLS-1$	

	/**
	 * The name of the default SDS workspace project.
	 */
	public static final String DEFAULT_PROJECT_NAME = "SDS"; //$NON-NLS-1$

	/**
	 * The name of the resource folder which contains the scripts.
	 */
	public static final String RESOURCE_SCRIPT_FOLDER_NAME = "scripts"; //$NON-NLS-1$

	/**
	 * The shared instance of this plugin activator.
	 */
	private static SdsPlugin _plugin;

	/**
	 * Change listener for SDS resources.
	 */
	private SdsResourceChangeListener _resourceChangeListener;

	/**
	 * Standard constructor.
	 */
	public SdsPlugin() {
		_plugin = this;
	}

	/**
	 * Returns the shared instance of this _plugin activator.
	 * 
	 * @return The shared instance of this _plugin activator.
	 */
	public static SdsPlugin getDefault() {
		return _plugin;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected void doStart(final BundleContext context) throws Exception {
		_resourceChangeListener = new SdsResourceChangeListener();

		// create the default SDS project
		ResourceService.getInstance().createWorkspaceProject(
				DEFAULT_PROJECT_NAME);

		// put the script rules into the workspace
		IProject scriptProject = ResourceService.getInstance()
				.createWorkspaceProject(RuleService.SCRIPT_PROJECT_NAME);
		ResourceService.getInstance()
				.copyResources(scriptProject,
						SdsPlugin.getDefault().getBundle(),
						RESOURCE_SCRIPT_FOLDER_NAME);
		
		// put the cursors project into the workspace
		ResourceService.getInstance().createWorkspaceProject(CursorService.CURSORS_PROJECT_NAME);

		// register the workspace listener that keeps track of script file
		// changes
		ResourceService.getInstance().addResourceChangeListener(
				_resourceChangeListener);

		// initialize the rules for the very first time
		if (RuleService.getInstance().isErrorOccurred()) {
			CentralLogger.getInstance().error(
					this,
					StringUtil.convertListToSingleString(RuleService
							.getInstance().getErrorMessages()));
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected void doStop(final BundleContext context) throws Exception {
		// de-register the workspace listener
		ResourceService.getInstance().removeResourceChangeListener(
				_resourceChangeListener);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
}
