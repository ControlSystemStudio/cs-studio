package org.csstudio.nams.configurator.treeviewer;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortGroupBean;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConfigurationContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IConfigurationNode) {
			return ((IConfigurationNode) parentElement).getChildren().toArray();
		}
		
		if(parentElement instanceof SortGroupBean){
			// TODO forstsetzen
//			return ((SortGroupBean)parentElement).get
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
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IConfigurationNode) {
			// TODO forstsetzen
//			if (!(inputElement instanceof IConfigurationNode)
//					|| ((IConfigurationNode) inputElement).getCategory().equals(
//							Categories.ROOT)) {
//				System.out
//						.println("ConfigurationContentProvider.getElements() WARNING: no CategoryNode for ROOT found");
//			}
			return ((IConfigurationNode) inputElement).getChildren().toArray();
		}
		return null;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
