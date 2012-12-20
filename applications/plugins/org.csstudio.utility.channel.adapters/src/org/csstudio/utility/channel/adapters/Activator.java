package org.csstudio.utility.channel.adapters;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This activator exists to force our adapter factory to work. The Properties
 * view uses the {@link IAdapterManager#getAdapter(Object, Class)} method which
 * only finds the adapter if the bundle that defines it has already been
 * activated. To make sure that our bundle is activated before we need it, it
 * defines an extension to <code>org.eclipse.ui.startup</code> which tells the
 * workbench to start this bundle when it starts.
 */
public class Activator extends AbstractUIPlugin implements IStartup {
	/**
	 * We don't actually need to do anything when we start. We just need this
	 * bundle to be activated so that the
	 * <code>org.eclipse.core.runtime.adapters</code> will work.
	 */
	@Override
	public void earlyStartup() {		
	}

}
