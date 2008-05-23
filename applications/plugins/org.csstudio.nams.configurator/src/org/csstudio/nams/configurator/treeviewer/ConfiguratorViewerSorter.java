package org.csstudio.nams.configurator.treeviewer;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Dieser Sorter Sortiert die TreeViewer Knoten alphabetisch, wobei der Knoten
 * "EmptyNodes" nach ganz unten sortiert wird
 * 
 * @author Eugen Reiswich
 * 
 */
public class ConfiguratorViewerSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IConfigurationNode
				&& e2 instanceof IConfigurationNode) {
			IConfigurationNode node1 = (IConfigurationNode) e1;
			IConfigurationNode node2 = (IConfigurationNode) e2;

			if (node1.getConfigurationType() == ConfigurationType.EMPTYGROUPS) {
				// node1 soll unter node2 angezeigt werden
				return +1;
			}

			if (node2.getConfigurationType() == ConfigurationType.EMPTYGROUPS) {
				// node2 soll soll unter node1 angezeigt werden
				return -1;
			}
		}

		return super.compare(viewer, e1, e2);
	}
}
