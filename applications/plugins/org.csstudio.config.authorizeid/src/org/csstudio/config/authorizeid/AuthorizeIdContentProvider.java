package org.csstudio.config.authorizeid;

import org.csstudio.config.authorizeid.AuthorizeIdEntry;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AuthorizeIdContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}
	 */
	public Object[] getElements(final Object inputElement) {
		if(inputElement instanceof AuthorizeIdEntry[]) {
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
