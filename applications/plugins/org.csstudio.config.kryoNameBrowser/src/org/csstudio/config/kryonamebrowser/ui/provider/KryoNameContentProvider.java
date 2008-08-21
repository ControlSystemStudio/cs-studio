package org.csstudio.config.kryonamebrowser.ui.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class KryoNameContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof List) {
			return ((List) inputElement).toArray();
		}

		return new Object[] {};
	}

	public void dispose() {
		// we can ignore this

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// we can ignore this

	}

}
