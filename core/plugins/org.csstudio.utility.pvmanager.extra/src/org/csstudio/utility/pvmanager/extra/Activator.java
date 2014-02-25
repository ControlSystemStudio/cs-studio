package org.csstudio.utility.pvmanager.extra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.formula.FormulaFunctionSet;
import org.epics.pvmanager.formula.FormulaRegistry;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceRegistry;

public class Activator extends Plugin {

	private static final Logger log = Logger.getLogger(Activator.class
			.getName());

	// The plug-in ID
	public static final String ID = "org.csstudio.utility.pvmanager.extra";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// Retrieve formula sets
		try {
			for (FormulaFunctionSet functionSet : configuredFormulaSets()) {
				FormulaRegistry.getDefault().registerFormulaFunctionSet(functionSet);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Couldn't configure PVManager with services",
					e);
		}

	}

	/**
	 * Retrieves the data sources that have been registered through the
	 * extension point.
	 * 
	 * @return the registered data sources
	 */
	public static List<FormulaFunctionSet> configuredFormulaSets() {
		try {
			List<FormulaFunctionSet> services = new ArrayList<>();
			IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.csstudio.utility.pvmanager.functionset");

			for (IConfigurationElement iConfigurationElement : config) {
				final Object o = iConfigurationElement
						.createExecutableExtension("functionset");
				if (o instanceof FormulaFunctionSet) {
					services.add((FormulaFunctionSet) o);
				}
			}
			return services;
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Could not retrieve configured formula sets for PVManager", e);
			return Collections.emptyList();
		}

	}

}
