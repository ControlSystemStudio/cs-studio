package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

class OlogContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		return (Object[]) inputElement;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}