package org.csstudio.nams.configurator.treeviewer;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationGroup;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConfigurationContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IConfigurationRoot) {
			return ((IConfigurationRoot) parentElement).getChildren().toArray();
		}

		if (parentElement instanceof IConfigurationGroup) {
			IConfigurationGroup groupNode = (IConfigurationGroup) parentElement;
			return groupNode.getChildren().toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof IConfigurationGroup) {
			IConfigurationGroup groupNode = (IConfigurationGroup) element;
			return groupNode.getParent();
		}

		if (element instanceof IConfigurationBean) {
			IConfigurationBean item = (IConfigurationBean) element;
			return item.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IConfigurationRoot) {
			return ((IConfigurationRoot) element).getChildren().size() > 0;
		}

		if (element instanceof IConfigurationGroup) {
			return ((IConfigurationGroup) element).getChildren().size() > 0;
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
