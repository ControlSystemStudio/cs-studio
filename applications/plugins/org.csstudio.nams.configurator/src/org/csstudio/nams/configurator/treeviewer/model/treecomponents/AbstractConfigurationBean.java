package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

abstract class AbstractConfigurationBean
		extends AbstractObservableBean implements ConfigurationBean {

	/**
	 * Liefertm den bezeichnenden Namen, der in einer UI für diese, konkrete
	 * Bean angezeigt werden soll. Der Name sollte den Inhalt repraesentieren.
	 * 
	 * @return Not {@code null}.
	 */
	public abstract String getDisplayName();
}
