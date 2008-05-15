package org.csstudio.nams.configurator.treeviewer;

import org.csstudio.nams.configurator.treeviewer.treecomponents.AbstractNode;
import org.csstudio.nams.configurator.treeviewer.treecomponents.Categories;
import org.csstudio.nams.configurator.treeviewer.treecomponents.CategoryNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConfigurationContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AbstractNode) {
			return ((CategoryNode)parentElement).getChildren();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof AbstractNode) {
			return ((CategoryNode)element).getChildren().length>0;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractNode) {
			if (!(inputElement instanceof CategoryNode) || ((CategoryNode)inputElement).getCategory().equals(Categories.ROOT)) {
				System.out
						.println("ConfigurationContentProvider.getElements() WARNING: no CategoryNode for ROOT found");
			}
			return ((CategoryNode)inputElement).getChildren();
		}
		return null;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
