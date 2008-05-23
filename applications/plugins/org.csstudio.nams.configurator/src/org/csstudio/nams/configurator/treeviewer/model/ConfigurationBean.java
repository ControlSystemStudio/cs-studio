package org.csstudio.nams.configurator.treeviewer.model;

public interface ConfigurationBean {

	/**
	 * Liefertm den eindeutigen, bezeichnenden Namen, der in einer UI für diese, konkrete
	 * Bean angezeigt werden soll. Der Name sollte den Inhalt repraesentieren.
	 * 
	 * @return Not {@code null}.
	 */
	public abstract String getDisplayName();
}