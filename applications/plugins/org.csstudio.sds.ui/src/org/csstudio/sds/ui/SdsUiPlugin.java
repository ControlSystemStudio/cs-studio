package org.csstudio.sds.ui;

import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.connection.ConnectionUtil;
import org.csstudio.sds.model.logic.RuleService;
import org.csstudio.sds.util.StringUtil;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class SdsUiPlugin extends AbstractCssUiPlugin {

	/**
	 * The ID of this _plugin.
	 */
	public static final String PLUGIN_ID = "org.csstudio.sds.ui"; //$NON-NLS-1$

	/**
	 * The ID of the edit parts extension point.
	 */
	public static final String EXTPOINT_WIDGET_EDITPARTS = PLUGIN_ID
			+ ".widgetEditParts"; //$NON-NLS-1$

	/**
	 * The ID of the graphical feedback factories extension point.
	 */
	public static final String EXTPOINT_GRAPHICAL_FEEDBACK_FACTORIES = PLUGIN_ID
			+ ".graphicalFeedbackFactories"; //$NON-NLS-1$

	/**
	 * The ID of the property descriptors extension point.
	 */
	public static final String EXTPOINT_PROPERTY_DESRIPTORS_FACTORIES = PLUGIN_ID
			+ ".propertyDescriptorFactories"; //$NON-NLS-1$

	/**
	 * The shared instance of this _plugin activator.
	 */
	private static SdsUiPlugin _plugin;

	/**
	 * The preference store to access the sds core preferences.
	 */
	private static IPreferenceStore _preferenceStore;

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
	 * TODO sh: this code is copied from CSSPlatformUiPlugin; it should better
	 * be reused.
	 * 
	 * Return the preference store of the sds core plugin.
	 * 
	 * @return The preference store of the sds core plugin.
	 */
	public static IPreferenceStore getCorePreferenceStore() {
		if (_preferenceStore == null) {
			String qualifier = SdsPlugin.getDefault().getBundle().getSymbolicName();
			_preferenceStore = new ScopedPreferenceStore(new InstanceScope(),
					qualifier);
			_preferenceStore
					.addPropertyChangeListener(new IPropertyChangeListener() {
						public void propertyChange(
								final PropertyChangeEvent event) {
							CentralLogger.getInstance().info(this,
									"Property [" + event.getProperty() //$NON-NLS-1$
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
	protected void doStart(final BundleContext context) throws Exception {
		// if there are errors within the scripts, tell the user!
		if (RuleService.getInstance().isErrorOccurred()) {
			List<String> errorMessages = RuleService.getInstance()
					.getErrorMessages();

			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					"SDS Scripting Errors", StringUtil
							.convertListToSingleString(errorMessages));
		}

		// Initialize Connection ModelLoadingUtil
		ConnectionUtil.getInstance().setDisplay(Display.getCurrent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStop(final BundleContext context) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
}
