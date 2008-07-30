package org.csstudio.nams.configurator.model;

import java.util.SortedSet;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.configurator.model.declaration.ConfigurationElementModelAccessService;
import org.csstudio.nams.configurator.model.declaration.ConfigurationsElementeAuflistung;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.logging.declaration.Logger;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfiguratorModelActivator extends AbstractBundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.configurator.model";

	@OSGiBundleActivationMethod
	public OSGiServiceOffers activateBundle(@OSGiService
	@Required
	final Logger logger) {
		final OSGiServiceOffers result = new OSGiServiceOffers();

		result.put(ConfigurationElementModelAccessService.class,
				new ConfigurationElementModelAccessService() {

					public SortedSet<AlarmbearbeiterDTO> getVisibleAlarmbearbeiterInAscendingOrder() {
						final ConfigurationsElementeAuflistung<AlarmbearbeiterDTO> auflistung = new ConfigurationsElementeAuflistung<AlarmbearbeiterDTO>();

						// TODO Auflistung füllen, ggf. von wo anders bezeihen...

						return auflistung.getVisibleElementsInAscendingOrder();
					}

				});

		return result;
	}

}
