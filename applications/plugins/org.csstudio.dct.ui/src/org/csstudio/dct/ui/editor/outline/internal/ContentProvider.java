package org.csstudio.dct.ui.editor.outline.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.internal.AbstractContainer;
import org.csstudio.dct.model.internal.Project;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for the input statistic table.
 * 
 * @author Sven Wende
 * 
 * @SuppressWarnings("unchecked")
 */
public class ContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		Object[] result = null;

		if (parentElement instanceof IFolder) {
			result = ((IFolder) parentElement).getMembers().toArray();
		} else if (parentElement instanceof AbstractContainer) {
			AbstractContainer container = (AbstractContainer) parentElement;
			List l = new ArrayList();
			l.addAll(container.getRecords());
			l.addAll(container.getInstances());
			result = l.toArray();
		}
		return result;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		boolean result = false;

		if (element instanceof IFolder) {
			result = ((IFolder) element).getMembers().size() > 0;
		} else if (element instanceof AbstractContainer) {
			AbstractContainer container = (AbstractContainer) element;
			result = (container.getRecords().size() + container.getInstances().size()) > 0;
		}
		return result;
	}

	public Object[] getElements(Object inputElement) {
		return ((Project) inputElement).getMembers().toArray();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}
}
