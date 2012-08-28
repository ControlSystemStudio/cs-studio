package org.csstudio.utility.toolbox;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.utility.toolbox.common.Constant;
import org.csstudio.utility.toolbox.guice.DependencyInjector;
import org.csstudio.utility.toolbox.guice.EntityManagerWrapper;
import org.csstudio.utility.toolbox.guice.PersistenceContextClearer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

public class ToolboxPlugin extends AbstractCssUiPlugin {

	private static ToolboxPlugin plugin;

	private IWorkbenchWindow window = null;

	private Display display = null;

	public ToolboxPlugin() {
		plugin = this;
	}

	public static ToolboxPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void doStart(BundleContext context) throws Exception {

		DependencyInjector.INSTANCE.startPersistService();

		EntityManagerWrapper emWrapper = DependencyInjector.INSTANCE.getInjector().getInstance(EntityManagerWrapper.class);
		PersistenceContextClearer.persistenceContextClearer.setEm(emWrapper);
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		window = workbench.getActiveWorkbenchWindow();
		display = window.getShell().getDisplay();
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(Constant.PLUGIN_ID, path);
	}

	@Override
	public String getPluginId() {
		return Constant.PLUGIN_ID;
	}

	public Display getDisplay() {
		return display;
	}

	public String getNameAndVersion() {
		return Constant.NAME + Constant.VERSION;
	}

	public Shell getShell() {
		return window.getShell();
	}

}