package org.csstudio.nams.configurator.model.declaration;

import java.util.SortedSet;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;

/**
 * Dieser Service bietet den UI Komponenten Zugriff auf die zugehörigen Modells.
 */
public interface ConfigurationElementModelAccessService {

	/**
	 * Liefert Alarmbearbeiter in aufsteigender Reihenfolge,
	 * 
	 * @see ConfigurationsElementeAuflistung#getVisibleElementsInAscendingOrder()
	 */
	public SortedSet<AlarmbearbeiterDTO> getVisibleAlarmbearbeiterInAscendingOrder();
}
