package de.desy.css.dal.tine;

import java.util.Properties;

import org.epics.css.dal.impl.DefaultApplicationContext;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class TINEApplicationContext extends DefaultApplicationContext {
	protected Properties configuration;
	
	public TINEApplicationContext(String name) {
		super(name);
		PlugUtilities.configureTINEPlug(getConfiguration());
	}
	
	
	
}
