package org.csstudio.utility.toolbox;

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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*
 * Version 1.0.RC3
 */
public class ToolboxPlugin extends AbstractUIPlugin {

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
    public void start(BundleContext context) throws Exception {
	   super.start(context);
	   IWorkbench workbench = PlatformUI.getWorkbench();
      window = workbench.getActiveWorkbenchWindow();
      display = window.getShell().getDisplay();
	   DependencyInjector.INSTANCE.startPersistService();
		EntityManagerWrapper emWrapper = DependencyInjector.INSTANCE.getInjector().getInstance(EntityManagerWrapper.class);
		PersistenceContextClearer.PERSISTENCE_CONTEXT_CLEARER.setEm(emWrapper);
	}

	@Override
    public void stop(BundleContext context) throws Exception {
	    super.stop(context);
		plugin = null;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(Constant.PLUGIN_ID, path);
	}

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