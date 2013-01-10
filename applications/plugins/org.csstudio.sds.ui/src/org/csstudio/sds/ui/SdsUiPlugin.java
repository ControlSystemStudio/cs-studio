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
package org.csstudio.sds.ui;

import java.util.List;

import org.csstudio.platform.simpledal.ProcessVariableAddressValidationServiceTracker;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.rules.RuleService;
import org.csstudio.sds.ui.internal.editor.newproperties.colorservice.ColorAndFontSaxHandler;
import org.csstudio.sds.ui.internal.editor.newproperties.colorservice.ColorAndFontService;
import org.csstudio.sds.ui.internal.editor.newproperties.colorservice.IColorAndFontService;
import org.csstudio.sds.ui.internal.preferences.AllowWriteAccessPreferenceListener;
import org.csstudio.sds.ui.internal.pvlistview.preferences.PvSearchFolderPreferenceService;
import org.csstudio.sds.ui.sdslibrary.preferences.LibraryFolderPreferenceService;
import org.csstudio.sds.util.StringUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Alexander Will
 * @version $Revision: 1.31 $
 * 
 */
public final class SdsUiPlugin extends AbstractUIPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(SdsUiPlugin.class);

	/**
	 * The ID of this _plugin.
	 */
	public static final String PLUGIN_ID = "org.csstudio.sds.ui"; //$NON-NLS-1$

	/**
	 * The ID of the edit parts extension point.
	 */
	public static final String EXTPOINT_WIDGET_EDITPARTS = PLUGIN_ID + ".widgetEditParts"; //$NON-NLS-1$

	/**
	 * The ID of the graphical feedback factories extension point.
	 */
	public static final String EXTPOINT_GRAPHICAL_FEEDBACK_FACTORIES = PLUGIN_ID + ".graphicalFeedbackFactories"; //$NON-NLS-1$

	/**
	 * The ID of the property descriptors extension point.
	 */
	public static final String EXTPOINT_PROPERTY_DESRIPTORS_FACTORIES = PLUGIN_ID + ".propertyDescriptorFactories"; //$NON-NLS-1$

	/**
	 * The shared instance of this _plugin activator.
	 */
	private static SdsUiPlugin _plugin;

	private static IPreferenceStore _preferenceStore;

	private IColorAndFontService _colorAndFontService;

	private LibraryFolderPreferenceService libraryFolderPreferenceService;
	private PvSearchFolderPreferenceService pvSearchFolderPreferenceService;

	private ProcessVariableAddressValidationServiceTracker pvAddressValidationServiceTracker;

	/**
	 * Standard constructor.
	 */
	public SdsUiPlugin() {
		_plugin = this;
	}

	/**
	 * Returns the shared instance of this _plugin activator.
	 * 
	 * @return The shared instance of this _plugin activator.
	 */
	public static SdsUiPlugin getDefault() {
		return _plugin;
	}

	/**
	 * 
	 * Return the preference store of the sds core plugin.
	 * 
	 * @return The preference store of the sds core plugin.
	 */
	public static IPreferenceStore getCorePreferenceStore() {
		if (_preferenceStore == null) {
			String qualifier = SdsPlugin.getDefault().getBundle().getSymbolicName();
			_preferenceStore = new ScopedPreferenceStore(new InstanceScope(), qualifier);
			_preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
				public void propertyChange(final PropertyChangeEvent event) {
					LOG.info("Property [" + event.getProperty() //$NON-NLS-1$
							+ "] changed from [" //$NON-NLS-1$
							+ event.getOldValue() + "] to [" //$NON-NLS-1$
							+ event.getNewValue() + "]"); //$NON-NLS-1$
				}
			});
		}
		return _preferenceStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		// if there are errors within the scripts, tell the user!
		if (RuleService.getInstance().isErrorOccurred()) {
			List<String> errorMessages = RuleService.getInstance().getErrorMessages();

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "SDS Scripting Errors", StringUtil
					.convertListToSingleString(errorMessages));
		}
		getCorePreferenceStore().addPropertyChangeListener(new AllowWriteAccessPreferenceListener());

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getProject("Settings").getFile("settings.xml");
		_colorAndFontService = new ColorAndFontService(file, new ColorAndFontSaxHandler());
		libraryFolderPreferenceService = new LibraryFolderPreferenceService(this.getPreferenceStore());
		pvSearchFolderPreferenceService = new PvSearchFolderPreferenceService(this.getPreferenceStore());
		
		pvAddressValidationServiceTracker = new ProcessVariableAddressValidationServiceTracker(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		pvAddressValidationServiceTracker.close();
	}

	public IColorAndFontService getColorAndFontService() {
		return _colorAndFontService;
	}
	
	public LibraryFolderPreferenceService getLibraryFolderPreferenceService() {
		return libraryFolderPreferenceService;
	}
	
	public PvSearchFolderPreferenceService getPvSearchFolderPreferenceService() {
		return pvSearchFolderPreferenceService;
	}

	public ProcessVariableAddressValidationServiceTracker getProcessVariableAddressValidationServiceTracker() {
		return this.pvAddressValidationServiceTracker;
	}
}
