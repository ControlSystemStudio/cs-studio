package org.csstudio.nams.configurator.treeviewer;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortgroupNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConfigurationContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IConfigurationNode) {
			return ((IConfigurationNode) parentElement).getChildren().toArray();
		}

		if (parentElement instanceof SortgroupNode) {
			SortgroupNode groupNode = (SortgroupNode) parentElement;
			return groupNode.getChildren().toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IConfigurationNode) {
			return ((IConfigurationNode) element).getChildren().size() > 0;
		}

		if (element instanceof SortgroupNode) {
			return ((SortgroupNode) element).getChildren().size() > 0;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return ((Collection) inputElement).toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
