package org.csstudio.nams.common.plugin.utils;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * An abstract bundle activator to provide common behaviour.
 * 
 * TODO Decide if use this abstract class or {@link BundleActivatorUtils}.
 * 
 * @deprecated Vererbung nicht nštig, somit ist eine Util Klasse sinnhafter.
 */
@Deprecated
public abstract class AbstractBundleActivator implements BundleActivator {

	final public void start(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	final public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
