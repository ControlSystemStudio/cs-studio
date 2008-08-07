package org.csstudio.utility.recordproperty;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class RecordPropertyContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}
	 */
	public Object[] getElements(final Object inputElement) {
		if(inputElement instanceof RecordPropertyEntry[]) {
			return (Object[]) inputElement;
		}
		return new Object[] {};
	}
	
	public void dispose() {
		
	}
	
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		// nothing to do
	}
}
