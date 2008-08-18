package org.csstudio.config.kryonamebrowser.ui.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class KryoNameContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof List) {
			return ((List) inputElement).toArray();
		}

		return new Object[] {};
	}

	@Override
	public void dispose() {
		// we can ignore this

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// we can ignore this

	}

}
