package org.epics.css.dal.tango;

import java.util.Properties;

import org.epics.css.dal.impl.DefaultApplicationContext;

/**
 * 
 * <code>TangoApplicationContext</code> is the default application
 * context for tango applications.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class TangoApplicationContext extends DefaultApplicationContext {
	protected Properties configuration;
	
	/**
	 * Constructs a new TangoApplicationContext.
	 * 
	 * @param name the name of the application that requires this context
	 */
	public TangoApplicationContext(String name) {
		super(name);
		PlugUtilities.configureTangoPlug(getConfiguration());
	}
}
