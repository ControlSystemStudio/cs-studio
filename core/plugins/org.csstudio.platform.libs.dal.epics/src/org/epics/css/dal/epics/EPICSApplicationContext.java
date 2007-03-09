/*
 * Copyright (c) 2006 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.epics.css.dal.epics;

import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simulation.SimulatorUtilities;

/**
 * Default EPICS application context.
 * Installs Simulator and EPICS (default) plugin.
 * @author ikriznar
 */
public class EPICSApplicationContext extends DefaultApplicationContext {

	/**
	 * Constructor.
	 * @param name	context name.
	 */
	public EPICSApplicationContext(String name) {
		super(name);
		SimulatorUtilities.configureSimulatorPlug(getConfiguration());
		PlugUtilities.configureEPICSPlug(getConfiguration());
	}

}
