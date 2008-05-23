package org.csstudio.nams.configurator.treeviewer.model;

public interface IConfigurationBean {

	/**
	 * Liefertm den eindeutigen, bezeichnenden Namen, der in einem TreeViewer
	 * für diese, konkrete Bean angezeigt werden soll. Der Name sollte den
	 * Inhalt repraesentieren.
	 * 
	 * Alle Elemente einer TreeView implementieren dieses Interface
	 * 
	 * @return Not {@code null}.
	 */
	public String getDisplayName();
}